package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.DefaultCollectionLookup;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private String definedInAttributeIdentifier = "defined-in";
    private ReflectionProvider reflectionProvider;
    private DefaultCollectionLookup defaultCollectionLookup;
    private InstanceResolver instanceResolver;

    public ReflectionConverter(ClassMapper classMapper, String classAttributeIdentifier, String definedInAttributeIdentifier,
                               ReflectionProvider reflectionProvider, DefaultCollectionLookup defaultCollectionLookup) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.definedInAttributeIdentifier = definedInAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
        this.defaultCollectionLookup = defaultCollectionLookup;
        instanceResolver = new InstanceResolver();
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Set seenFields = new HashSet();
        final String defaultCollectionField = defaultCollectionLookup.getDefaultCollectionField(source.getClass());
        reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor() {
            public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                if (newObj != null) {
                    if (defaultCollectionField != null && defaultCollectionField.equals(fieldName)) {
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
        Collection defaultCollection = getDefaultCollection(result);
        SeenFields seenFields = new SeenFields();

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
            } else if (defaultCollection != null) {
                defaultCollection.add(value);
            }

            reader.moveUp();
        }
        return instanceResolver.resolve(result);
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

    private Collection getDefaultCollection(Object instance) {
        String defaultCollectionFieldName = defaultCollectionLookup.getDefaultCollectionField(instance.getClass());
        if (defaultCollectionFieldName != null) {
            Collection result = new ArrayList();
            reflectionProvider.writeField(instance, defaultCollectionFieldName, result, null);
            return result;
        } else {
            return null;
        }
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
        }
    }
}
