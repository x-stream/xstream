package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private ReflectionProvider reflectionProvider;

    public ReflectionConverter(ClassMapper classMapper,String classAttributeIdentifier, ReflectionProvider reflectionProvider) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void toXML(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        reflectionProvider.eachSerializableField(source.getClass(), new ReflectionProvider.Block() {
            public void visit(String fieldName, Class fieldType) {
                Object newObj = reflectionProvider.readField(source, fieldName);
                if (newObj != null) {
                    writer.startNode(classMapper.mapNameToXML(fieldName));

                    Class actualType = newObj.getClass();

                    Class defaultType = classMapper.lookupDefaultType(fieldType);
                    if (!actualType.equals(defaultType)) {
                        writer.addAttribute(classAttributeIdentifier, classMapper.lookupName(actualType));
                    }

                    context.convertAnother(newObj);

                    writer.endNode();
                }
            }
        });
    }

    public Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object result = context.currentObject();

        if (result == null) {
            result = reflectionProvider.newInstance(context.getRequiredType());
        }

        while (reader.getNextChildNode()) {
            String fieldName = classMapper.mapNameFromXML(reader.getNodeName());

            Class type;
            String classAttribute = reader.getAttribute(classAttributeIdentifier);
            if (classAttribute == null) {
                type = reflectionProvider.getFieldType(result, fieldName);
            } else {
                type = classMapper.lookupType(classAttribute);
            }

            Object fieldValue = context.convertAnother(type);

            reflectionProvider.writeField(result, fieldName, fieldValue);

            reader.getParentNode();
        }
        return result;
    }

}
