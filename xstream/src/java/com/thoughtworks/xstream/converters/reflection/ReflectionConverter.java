package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ImplicitCollectionMapper;
import com.thoughtworks.xstream.alias.ImplicitCollectionDef;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private String definedInAttributeIdentifier = "defined-in";
    private ReflectionProvider reflectionProvider;
    private ImplicitCollectionMapper implicitCollectionMapper;
    private SerializationMethodInvoker serializationMethodInvoker;

    // Todo:
    //  - Ensure readObject()/writeObject() include the heirarchy (start at superclass)
    //  - putFields()/writeFields()/readFields()
    //  - ObjectStreamField[] serialPersistentFields
    //  - ObjectInputValidation

    public ReflectionConverter(ClassMapper classMapper, String classAttributeIdentifier, String definedInAttributeIdentifier,
                               ReflectionProvider reflectionProvider, ImplicitCollectionMapper implicitCollectionMapper) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.definedInAttributeIdentifier = definedInAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
        this.implicitCollectionMapper = implicitCollectionMapper;
        serializationMethodInvoker = new SerializationMethodInvoker();
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Object replacedSource = serializationMethodInvoker.callWriteReplace(source);
        final Set seenFields = new HashSet();

        final boolean[] writtenToStream = { false };
        final Class[] currentClass = new Class[1];

        CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {

            public void writeToStream(Object object) {
                if (!writtenToStream[0]) {
                    writer.startNode("object.stream");
                    if (!currentClass[0].equals(replacedSource.getClass())) {
                        writer.addAttribute("defined-in", classMapper.lookupName(currentClass[0]));
                    }
                    writtenToStream[0] = true;
                }
                if (object == null) {
                    writer.startNode("null");
                    writer.endNode();
                } else {
                    writer.startNode(classMapper.lookupName(object.getClass()));
                    context.convertAnother(object);
                    writer.endNode();
                }
            }

            public void defaultWriteObject() {
                if (writtenToStream[0]) {
                    writer.endNode();
                    writtenToStream[0] = false;
                }
                reflectionProvider.visitSerializableFields(replacedSource, new ReflectionProvider.Visitor() {
                    public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                        if (newObj != null) {
                            ImplicitCollectionDef def = implicitCollectionMapper.getImplicitCollectionDefForFieldName(definedIn, fieldName);
                            if (def != null) {
                                if (def.getItemFieldName() != null) {
                                    ArrayList list = (ArrayList) newObj;
                                    for (Iterator iter = list.iterator(); iter.hasNext();) {
                                        Object obj = iter.next();
                                        writeField(def.getItemFieldName(), def.getItemType(), definedIn, obj);
                                    }
                                } else {
                                    context.convertAnother(newObj);
                                }
                            } else {
                                writeField(fieldName, fieldType, definedIn, newObj);
                                seenFields.add(fieldName);
                            }
                        }
                    }

                    private void writeField(String fieldName, Class fieldType, Class definedIn, Object newObj) {
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
                    }
                });
            }
        };

        currentClass[0] = replacedSource.getClass();
        //for(currentClass[0] = replacedSource.getClass(); currentClass[0] != null; currentClass[0] = currentClass[0].getSuperclass()) {
            if (serializationMethodInvoker.supportsWriteObject(currentClass[0])) {
                ObjectOutputStream objectOutputStream = CustomObjectOutputStream.getInstance(context, callback);
                serializationMethodInvoker.callWriteObject(currentClass[0], replacedSource, objectOutputStream);
                if (writtenToStream[0]) {
                    writer.endNode();
                    writtenToStream[0] = false;
                }
            } else {
                callback.defaultWriteObject();
            }
        //}

    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = instantiateNewInstance(context);
        final SeenFields seenFields = new SeenFields();

        final boolean[] readFromStream = { false };

        CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback() {

            public Object deserialize() {
                if (!readFromStream[0]) {
                    reader.moveDown();
                    readFromStream[0] = true;
                }
                reader.moveDown();
                Class type = classMapper.lookupType(reader.getNodeName());
                Object value = context.convertAnother(result, type);
                reader.moveUp();
                return value;
            }

            public void defaultReadObject() {
                if (readFromStream[0]) {
                    reader.moveUp();
                    readFromStream[0] = false;
                }
                Map implicitCollectionsForCurrentObject = null;
                while (reader.hasMoreChildren()) {
                    reader.moveDown();

                    String nodeName = reader.getNodeName();
                    if (nodeName.equals("object.stream")) {
                        readFromStream[0] = true;
                        break;
                    }
                    String fieldName = classMapper.mapNameFromXML(reader.getNodeName());

                    Class classDefiningField = determineWhichClassDefinesField(reader);
                    boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(fieldName, result.getClass());

                    Class type = determineType(reader, fieldExistsInClass, result, fieldName, classDefiningField);
                    Object value = context.convertAnother(result, type);

                    if (fieldExistsInClass) {
                        reflectionProvider.writeField(result, fieldName, value, classDefiningField);
                        seenFields.add(classDefiningField, fieldName);
                    } else {
                        implicitCollectionsForCurrentObject = writeValueToImplicitCollection(context, value, implicitCollectionsForCurrentObject, result, fieldName);
                    }

                    reader.moveUp();
                }
            }

        };

        if (serializationMethodInvoker.supportsReadObject(result.getClass())) {
            ObjectInputStream objectInputStream = CustomObjectInputStream.getInstance(context, callback);
            serializationMethodInvoker.callReadObject(result, objectInputStream);
            if (readFromStream[0]) {
                reader.moveUp();
                readFromStream[0] = false;
            }
        } else {
            callback.defaultReadObject();
        }

        return serializationMethodInvoker.callReadResolve(result);
    }


    private Map writeValueToImplicitCollection(UnmarshallingContext context, Object value, Map implicitCollections, Object result, String itemFieldName) {
        String fieldName = implicitCollectionMapper.getFieldNameForItemTypeAndName(context.getRequiredType(), value.getClass(), itemFieldName);
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
            Class itemType = implicitCollectionMapper.getItemTypeForItemFieldName(result.getClass(), fieldName);
            if (itemType != null) {
            	return itemType;
            } else {
            	return classMapper.lookupType(reader.getNodeName());
            }
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
