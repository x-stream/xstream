package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ImplicitCollectionMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private String definedInAttributeIdentifier = "defined-in";
    private ReflectionProvider reflectionProvider;
    private ImplicitCollectionMapper implicitCollectionMapper;
    private InstanceResolver instanceResolver;
    private Map writeObjectMethodCache = new HashMap(); // should be soft but Joe told me off - DN

    public ReflectionConverter(ClassMapper classMapper, String classAttributeIdentifier, String definedInAttributeIdentifier,
                               ReflectionProvider reflectionProvider, ImplicitCollectionMapper implicitCollectionMapper) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.definedInAttributeIdentifier = definedInAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
        this.implicitCollectionMapper = implicitCollectionMapper;
        instanceResolver = new InstanceResolver();
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (hasWriteObjectMethod(source)) {
            try {

                Method writeObjectMethod = getWriteObjectMethod(source);

                CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {
                    public void writeToStream(Object object) {
                        if (object == null) {
                            writer.startNode("stream.null");
                            writer.endNode();
                        } else {
                            writer.startNode("stream." + classMapper.lookupName(object.getClass()));
                            context.convertAnother(object);
                            writer.endNode();
                        }
                    }

                    public void defaultWriteObject() {
                        serializeEachField(source, context, writer);
                    }

                };

                ObjectOutputStream objectOutputStream = createCustomObjectOutputStream(callback, context);
                writeObjectMethod.setAccessible(true);
                writeObjectMethod.invoke(source, new Object[]{objectOutputStream});
            } catch (IllegalAccessException e) {
                throw new ConversionException("Could not call " + source.getClass().getName() + ".writeObject()", e);
            } catch (InvocationTargetException e) {
                throw new ConversionException("Could not call " + source.getClass().getName() + ".writeObject()", e);
            }

        } else {

            serializeEachField(source, context, writer);

        }

    }

    private boolean hasWriteObjectMethod(Object source) {
        return getWriteObjectMethod(source) != null;
    }

    private Method getWriteObjectMethod(Object source) {
        Object key = source.getClass();
        Method result = null;

        if (writeObjectMethodCache.containsKey(key)) {
            return (Method) writeObjectMethodCache.get(key);
        }
        else {
            try {
                result = source.getClass().getDeclaredMethod("writeObject", new Class[]{ObjectOutputStream.class});
            } catch (NoSuchMethodException e) {
                result = null;
            }
            writeObjectMethodCache.put(key, result);
        }
        return result;
    }

    private ObjectOutputStream createCustomObjectOutputStream(CustomObjectOutputStream.StreamCallback callback, final MarshallingContext context) {
        final String key = "Cached CustomObjectOutputStream";
        CustomObjectOutputStream objectOutputStream = (CustomObjectOutputStream) context.get(key);
        if (objectOutputStream == null) {
            objectOutputStream = CustomObjectOutputStream.create(callback);
            context.put(key, objectOutputStream);
        } else {
            objectOutputStream.setCallback(callback);
        }
        return objectOutputStream;
    }

    private void serializeEachField(final Object source, final MarshallingContext context, final HierarchicalStreamWriter writer) {
        final Set seenFields = new HashSet();
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                if (newObj != null) {
                    if (implicitCollectionMapper.isImplicitCollectionField(definedIn, fieldName)) {
                        context.convertAnother(newObj);
                    } else {
                        writer.startNode(classMapper.mapNameToXML(fieldName));

                        Class actualType = newObj.getClass();

                        Class defaultType = classMapper.lookupDefaultType(fieldType);
                        if (!actualType.equals(defaultType)) {
                            writer.addAttribute(classAttributeIdentifier, classMapper.lookupName(actualType));
                        }

                        if (seenFields.contains(fieldName)) {
                            writer.addAttribute(definedInAttributeIdentifier, classMapper.lookupName(definedIn));
                        }
                        context.convertAnother(newObj);

                        writer.endNode();
                        seenFields.add(fieldName);
                    }
                }
            }
        });
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object result = instantiateNewInstance(context);
        SeenFields seenFields = new SeenFields();
        Map implicitCollectionsForCurrentObject = null;

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String fieldName = classMapper.mapNameFromXML(reader.getNodeName());

            Class classDefiningField = determineWhichClassDefinesField(reader);
            boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(fieldName, result.getClass());

            Class type = determineType(reader, fieldExistsInClass, result, fieldName, classDefiningField);
            Object value = context.convertAnother(result, type);

            if (fieldExistsInClass) {
                reflectionProvider.writeField(result, fieldName, value, classDefiningField);
                seenFields.add(classDefiningField, fieldName);
            } else {
                implicitCollectionsForCurrentObject = writeValueToImplicitCollection(context, value, implicitCollectionsForCurrentObject, result);
            }

            reader.moveUp();
        }
        return instanceResolver.resolve(result);
    }

    private Map writeValueToImplicitCollection(UnmarshallingContext context, Object value, Map implicitCollections, Object result) {
        String fieldName = implicitCollectionMapper.implicitCollectionFieldForType(context.getRequiredType(), value.getClass());
        if (fieldName != null) {
            if (implicitCollections == null) {
                implicitCollections = new HashMap(); // lazy instantiation
            }
            Collection collection = (Collection) implicitCollections.get(fieldName);
            if (collection == null) {
                collection = new ArrayList();
                reflectionProvider.writeField(result, fieldName, collection, null);
                implicitCollections.put(fieldName, collection);
            }
            collection.add(value);
        }
        return implicitCollections;
    }

    private Class determineWhichClassDefinesField(HierarchicalStreamReader reader) {
        String definedIn = reader.getAttribute(definedInAttributeIdentifier);
        return definedIn == null ? null : classMapper.lookupType(definedIn);
    }

    private Object instantiateNewInstance(UnmarshallingContext context) {
        Object result = context.currentObject();
        if (result == null) {
            result = reflectionProvider.newInstance(context.getRequiredType());
        }
        return result;
    }

    private static class SeenFields {

        private Set seen = new HashSet();

        public void add(Class definedInCls, String fieldName) {
            String uniqueKey = fieldName;
            if (definedInCls != null) {
                uniqueKey += " [" + definedInCls.getName() + "]";
            }
            if (seen.contains(uniqueKey)) {
                throw new DuplicateFieldException(uniqueKey);
            } else {
                seen.add(uniqueKey);
            }
        }

    }

    private Class determineType(HierarchicalStreamReader reader, boolean validField, Object result, String fieldName, Class definedInCls) {
        String classAttribute = reader.getAttribute(classAttributeIdentifier);
        if (classAttribute != null) {
            return classMapper.lookupType(classAttribute);
        } else if (!validField) {
            return classMapper.lookupType(reader.getNodeName());
        } else {
            return classMapper.lookupDefaultType(reflectionProvider.getFieldType(result, fieldName, definedInCls));
        }
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
        }
    }
}
