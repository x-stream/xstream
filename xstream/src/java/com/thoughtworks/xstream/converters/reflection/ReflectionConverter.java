package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;
import java.util.Iterator;

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
        reflectionProvider.eachSerializableFields(source.getClass(), new ReflectionProvider.Block() {
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

                    writer.startNode();
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
            Iterator fields = reflectionProvider.listSerializableFields(result.getClass());
            Field field = null;
            while (fields.hasNext()) {
                Field tmp = (Field) fields.next();
                if (tmp.getName().equals(fieldName)) {
                    field = tmp;
                    break;
                }
            }
            if (field == null) {
                throw new ConversionException("No such field " + result.getClass() + "." + fieldName);
            }

            Class type;
            String classAttribute = reader.getAttribute(classAttributeIdentifier);
            if (classAttribute == null) {
                type = field.getType();
            } else {
                type = classMapper.lookupType(classAttribute);
            }

            Object fieldValue = context.convertAnother(type);

            try {
                field.setAccessible(true);
                field.set(result, fieldValue);
            } catch (IllegalAccessException e) {
                throw new ConversionException(
                        "Cannot access field " + type + "." + field.getName(), e);
            }

            reader.getParentNode();
        }
        return result;
    }


}
