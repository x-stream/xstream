package com.thoughtworks.xstream.converters.reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReflectionConverter implements Converter {

    private final Mapper mapper;
    private final ReflectionProvider reflectionProvider;
    private final SerializationMethodInvoker serializationMethodInvoker;
    private transient ReflectionProvider pureJavaReflectionProvider;

    public ReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        serializationMethodInvoker = new SerializationMethodInvoker();
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(Object original, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Object source = serializationMethodInvoker.callWriteReplace(original);

        if (source.getClass() != original.getClass()) {
            writer.addAttribute(mapper.attributeForReadResolveField(), mapper.serializedClass(source.getClass()));
        }

        final Set seenFields = new HashSet();
        final Set seenAsAttributes = new HashSet();

        // Attributes might be preferred to child elements ...
         reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            public void visit(String fieldName, Class type, Class definedIn, Object value) {
                SingleValueConverter converter = mapper.getConverterFromItemType(type);                
                if (converter != null) {
                    writer.addAttribute(fieldName, converter.toString(value));
                    seenAsAttributes.add(fieldName);
                }
            }
        });

        // Child Elements not covered already processed as Attributes ...
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                if (!seenAsAttributes.contains(fieldName) && newObj != null) {
                    Mapper.ImplicitCollectionMapping mapping = mapper.getImplicitCollectionDefForFieldName(source.getClass(), fieldName);
                    if (mapping != null) {
                        if (mapping.getItemFieldName() != null) {
                            Collection list = (Collection) newObj;
                            for (Iterator iter = list.iterator(); iter.hasNext();) {
                                Object obj = iter.next();
                                writeField(mapping.getItemFieldName(), mapping.getItemType(), definedIn, obj);
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
                if (!mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }
                writer.startNode(mapper.serializedMember(definedIn, fieldName));

                Class actualType = newObj.getClass();

                Class defaultType = mapper.defaultImplementationOf(fieldType);
                if (!actualType.equals(defaultType)) {
                    writer.addAttribute(mapper.attributeForImplementationClass(), mapper.serializedClass(actualType));
                }

                if (seenFields.contains(fieldName)) {
                    writer.addAttribute(mapper.attributeForClassDefiningField(), mapper.serializedClass(definedIn));
                }

                if (source != newObj) {
                    context.convertAnother(newObj);
                } else {
                    writer.addAttribute("self", "");
                }
                writer.endNode();
            }

        });
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = instantiateNewInstance(context, reader.getAttribute(mapper.attributeForReadResolveField()));
        final SeenFields seenFields = new SeenFields();
        Iterator it = reader.getAttributeNames();

        // Process attributes before recursing into child elements.
        while (it.hasNext()) {
            String attrName = (String) it.next();
            SingleValueConverter converter = mapper.getConverterFromAttribute(attrName);            
            if (converter != null) {
                Object value = converter.fromString(reader.getAttribute(attrName));
                Class classDefiningField = determineWhichClassDefinesField(reader);
                boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(attrName, result.getClass());
                if (fieldExistsInClass) {
                    reflectionProvider.writeField(result, attrName, value, classDefiningField);
                    seenFields.add(classDefiningField, attrName);
                }
            }
        }

        Map implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String fieldName = mapper.realMember(result.getClass(), reader.getNodeName());

            Class classDefiningField = determineWhichClassDefinesField(reader);
            boolean fieldExistsInClass = reflectionProvider.fieldDefinedInClass(fieldName, result.getClass());

            Class type = determineType(reader, fieldExistsInClass, result, fieldName, classDefiningField);
            final Object value;
            String self = reader.getAttribute("self");
            if (self == null) {
                value = context.convertAnother(result, type);
            } else {
                value = result;
            }

            if (fieldExistsInClass) {
                reflectionProvider.writeField(result, fieldName, value, classDefiningField);
                seenFields.add(classDefiningField, fieldName);
            } else {
                implicitCollectionsForCurrentObject = writeValueToImplicitCollection(context, value, implicitCollectionsForCurrentObject, result, fieldName);
            }

            reader.moveUp();
        }

        return serializationMethodInvoker.callReadResolve(result);
    }


    private Map writeValueToImplicitCollection(UnmarshallingContext context, Object value, Map implicitCollections, Object result, String itemFieldName) {
        String fieldName = mapper.getFieldNameForItemTypeAndName(context.getRequiredType(), value.getClass(), itemFieldName);
        if (fieldName != null) {
            if (implicitCollections == null) {
                implicitCollections = new HashMap(); // lazy instantiation
            }
            Collection collection = (Collection) implicitCollections.get(fieldName);
            if (collection == null) {
                Class fieldType = mapper.defaultImplementationOf(reflectionProvider.getFieldType(result, fieldName, null));
                if (!Collection.class.isAssignableFrom(fieldType)) {
                    throw new ObjectAccessException("Field " + fieldName + " of " + result.getClass().getName() +
                            " is configured for an implicit Collection, but field is of type " + fieldType.getName());
                }
                if (pureJavaReflectionProvider == null) {
                    pureJavaReflectionProvider = new PureJavaReflectionProvider();
                }
                collection = (Collection)pureJavaReflectionProvider.newInstance(fieldType);
                reflectionProvider.writeField(result, fieldName, collection, null);
                implicitCollections.put(fieldName, collection);
            }
            collection.add(value);
        }
        return implicitCollections;
    }

    private Class determineWhichClassDefinesField(HierarchicalStreamReader reader) {
        String definedIn = reader.getAttribute(mapper.attributeForClassDefiningField());
        return definedIn == null ? null : mapper.realClass(definedIn);
    }

    private Object instantiateNewInstance(UnmarshallingContext context, String readResolveValue) {
        Object currentObject = context.currentObject();
        if (currentObject != null) {
            return currentObject;
        } else if (readResolveValue != null) {
            return reflectionProvider.newInstance(mapper.realClass(readResolveValue));
        } else {
            return reflectionProvider.newInstance(context.getRequiredType());
        }
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
        String classAttribute = reader.getAttribute(mapper.attributeForImplementationClass());
        if (classAttribute != null) {
            return mapper.realClass(classAttribute);
        } else if (!validField) {
            Class itemType = mapper.getItemTypeForItemFieldName(result.getClass(), fieldName);
            if (itemType != null) {
                return itemType;
            } else {
                return mapper.realClass(reader.getNodeName());
            }
        } else {
            return mapper.defaultImplementationOf(reflectionProvider.getFieldType(result, fieldName, definedInCls));
        }
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
        }
    }
}
