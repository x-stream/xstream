package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashSet;
import java.util.Set;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private ReflectionProvider reflectionProvider;
    private static final String DEFINED_IN = "defined-in";

    public ReflectionConverter(ClassMapper classMapper, String classAttributeIdentifier, ReflectionProvider reflectionProvider) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Set seenFields = new HashSet();
        reflectionProvider.readSerializableFields(source, new ReflectionProvider.Block() {
            public void visit(String fieldName, Class fieldType, Class definedIn, Object newObj) {
                if (newObj != null) {
                    writer.startNode(classMapper.mapNameToXML(fieldName));

                    Class actualType = newObj.getClass();

                    Class defaultType = classMapper.lookupDefaultType(fieldType);
                    if (!actualType.equals(defaultType)) {
                        writer.addAttribute(classAttributeIdentifier, classMapper.lookupName(actualType));
                    }

                    if (seenFields.contains(fieldName)) {
                        writer.addAttribute(DEFINED_IN, classMapper.lookupName(definedIn));
                    }
                    context.convertAnother(newObj);

                    writer.endNode();
                    seenFields.add(fieldName);
                }
            }
        });
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object result = context.currentObject();

        if (result == null) {
            result = reflectionProvider.newInstance(context.getRequiredType());
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String fieldName = classMapper.mapNameFromXML(reader.getNodeName());

            Class type;
            String classAttribute = reader.getAttribute(classAttributeIdentifier);
            if (classAttribute == null) {
                type = classMapper.lookupDefaultType(reflectionProvider.getFieldType(result, fieldName));
            } else {
                type = classMapper.lookupType(classAttribute);
            }

            String definedIn = reader.getAttribute("defined-in");

            Object fieldValue = context.convertAnother(result, type);

            if (definedIn != null) {
                reflectionProvider.writeField(result, fieldName, fieldValue, classMapper.lookupType(definedIn));
            } else {
                reflectionProvider.writeField(result, fieldName, fieldValue);
            }

            reader.moveUp();
        }
        return result;
    }

}
