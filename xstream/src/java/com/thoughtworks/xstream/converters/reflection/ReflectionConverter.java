package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.DefaultCollectionLookup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private String definedInAttributeIdentifier = "defined-in";
    private ReflectionProvider reflectionProvider;
    private DefaultCollectionLookup defaultCollectionLookup;

    public ReflectionConverter(ClassMapper classMapper, String classAttributeIdentifier, String definedInAttributeIdentifier, ReflectionProvider reflectionProvider, DefaultCollectionLookup defaultCollectionLookup) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.definedInAttributeIdentifier = definedInAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
        this.defaultCollectionLookup = defaultCollectionLookup;
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
        Object result = context.currentObject();

        if (result == null) {
            result = reflectionProvider.newInstance(context.getRequiredType());
        }

        Collection defaultCollection = getDefaultCollection(result);

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String fieldName = classMapper.mapNameFromXML(reader.getNodeName());

            String definedIn = reader.getAttribute(definedInAttributeIdentifier);
            Class definedInCls = definedIn == null ? null : classMapper.lookupType(definedIn);

            boolean validField = reflectionProvider.fieldDefinedInClass(fieldName, result.getClass());

            Class type;
            String classAttribute = reader.getAttribute(classAttributeIdentifier);
            if (classAttribute != null) {
                type = classMapper.lookupType(classAttribute);
            } else if (!validField) {
                type = classMapper.lookupType(reader.getNodeName());
            } else {
                type = classMapper.lookupDefaultType(reflectionProvider.getFieldType(result, fieldName, definedInCls));
            }

            Object fieldValue = context.convertAnother(result, type);

            if (validField) {
                reflectionProvider.writeField(result, fieldName, fieldValue, definedInCls);
            } else if (defaultCollection != null) {
                defaultCollection.add(fieldValue);
            }

            reader.moveUp();
        }
        return new InstanceResolver().resolve(result);
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

}
