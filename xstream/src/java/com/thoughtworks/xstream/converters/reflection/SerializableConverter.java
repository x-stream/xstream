package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Emulates the mechanism used by standard Java Serialization for classes that implement java.io.Serializable AND
 * implement a custom readObject()/writeObject() method.
 *
 * <h3>Supported features of serialization</h3>
 * <ul>
 *   <li>readObject(), writeObject()</li>
 *   <li>class inheritance</li>
 *   <li>readResolve(), writeReplace()</li>
 * </ul>
 *
 * <h3>Currently unsupported features</h3>
 * <ul>
 *   <li>putFields(), writeFields(), readFields()</li>
 *   <li>ObjectStreamField[] serialPersistentFields</li>
 *   <li>ObjectInputValidation</li>
 * </ul>
 *
 * @author Joe Walnes
 */
public class SerializableConverter implements Converter {

    private final SerializationMethodInvoker serializationMethodInvoker = new SerializationMethodInvoker();
    private final ClassMapper classMapper;
    private final ReflectionProvider reflectionProvider;

    private static final String ELEMENT_NULL = "null";
    private static final String ELEMENT_DEFAULT = "default";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_SERIALIZATION = "serialization";
    private static final String ATTRIBUTE_VALUE_CUSTOM = "custom";

    public SerializableConverter(ClassMapper classMapper, ReflectionProvider reflectionProvider) {
        this.classMapper = classMapper;
        this.reflectionProvider = reflectionProvider;
    }

    public boolean canConvert(Class type) {
        return Serializable.class.isAssignableFrom(type)
          && ( serializationMethodInvoker.supportsReadObject(type, true)
            || serializationMethodInvoker.supportsWriteObject(type, true) );
    }

    public void marshal(Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Object replacedSource = serializationMethodInvoker.callWriteReplace(source);

        writer.addAttribute(ATTRIBUTE_SERIALIZATION, ATTRIBUTE_VALUE_CUSTOM);

        // this is an array as it's a non final value that's accessed from an anonymous inner class.
        final Class[] currentType = new Class[1];

        CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {

            public void writeToStream(Object object) {
                if (object == null) {
                    writer.startNode(ELEMENT_NULL);
                    writer.endNode();
                } else {
                    writer.startNode(classMapper.lookupName(object.getClass()));
                    context.convertAnother(object);
                    writer.endNode();
                }
            }

            public void defaultWriteObject() {
                writer.startNode(ELEMENT_DEFAULT);

                reflectionProvider.visitSerializableFields(replacedSource, new ReflectionProvider.Visitor() {
                    public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                        if (definedIn == currentType[0] && newObj != null) {
                            writer.startNode(classMapper.mapNameToXML(fieldName));

                            Class actualType = newObj.getClass();
                            Class defaultType = classMapper.lookupDefaultType(fieldType);
                            if (!actualType.equals(defaultType)) {
                                writer.addAttribute(ATTRIBUTE_CLASS, classMapper.lookupName(actualType));
                            }

                            context.convertAnother(newObj);

                            writer.endNode();
                        }
                    }
                });
                writer.endNode();
            }
        };

        currentType[0] = replacedSource.getClass();
        while (currentType[0] != null) {
            writer.startNode(classMapper.lookupName(currentType[0]));
            if (serializationMethodInvoker.supportsWriteObject(currentType[0], false)) {
                ObjectOutputStream objectOutputStream = CustomObjectOutputStream.getInstance(context, callback);
                serializationMethodInvoker.callWriteObject(currentType[0], replacedSource, objectOutputStream);
            } else {
                callback.defaultWriteObject();
            }
            currentType[0] = currentType[0].getSuperclass();
            writer.endNode();
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = reflectionProvider.newInstance(context.getRequiredType());

        // this is an array as it's a non final value that's accessed from an anonymous inner class.
        final Class[] currentType = new Class[1];

        if (!ATTRIBUTE_VALUE_CUSTOM.equals(reader.getAttribute(ATTRIBUTE_SERIALIZATION))) {
            throw new ConversionException("Cannot deserialize object with new readObject()/writeObject() methods");
        }

        CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback() {
            public Object deserialize() {
                reader.moveDown();
                Class type = classMapper.lookupType(reader.getNodeName());
                Object value = context.convertAnother(result, type);
                reader.moveUp();
                return value;
            }

            public void defaultReadObject() {
                reader.moveDown();
                if (!reader.getNodeName().equals(ELEMENT_DEFAULT)) {
                    throw new ConversionException("Expected <" + ELEMENT_DEFAULT + "/> element in readObject() stream");
                }
                while (reader.hasMoreChildren()) {
                    reader.moveDown();

                    Class type;
                    String fieldName = classMapper.mapNameFromXML(reader.getNodeName());
                    String classAttribute = reader.getAttribute(ATTRIBUTE_CLASS);
                    if (classAttribute != null) {
                        type = classMapper.lookupType(classAttribute);
                    } else {
                        type = classMapper.lookupDefaultType(reflectionProvider.getFieldType(result, fieldName, currentType[0]));
                    }

                    Object value = context.convertAnother(result, type);
                    reflectionProvider.writeField(result, fieldName, value, currentType[0]);

                    reader.moveUp();
                }
                reader.moveUp();
            }
        };

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            currentType[0] = classMapper.lookupType(reader.getNodeName());
            if (serializationMethodInvoker.supportsReadObject(currentType[0], false)) {
                ObjectInputStream objectInputStream = CustomObjectInputStream.getInstance(context, callback);
                serializationMethodInvoker.callReadObject(currentType[0], result, objectInputStream);
            } else {
                callback.defaultReadObject();
            }
            reader.moveUp();
        }

        return serializationMethodInvoker.callReadResolve(result);
    }

}
