package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ReflectionConverter implements Converter {

    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private ObjectFactory objectFactory;

    public ReflectionConverter(ClassMapper classMapper,String classAttributeIdentifier, ObjectFactory objectFactory) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.objectFactory = objectFactory;
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Iterator fields = getFields(source.getClass());
        while (fields.hasNext()) {
            Field field = (Field) fields.next();
            field.setAccessible(true);
            try {
                Object newObj = field.get(source);
                if (newObj != null) {
                    writeFieldAsXML(context, field, newObj, writer);
                }
            } catch (IllegalAccessException e) {
                throw new ConversionException(
                        "Cannot access field " + source.getClass() + "." + field.getName(), e);
            }
        }
    }

    private void writeFieldAsXML(MarshallingContext context, Field field, Object obj, HierarchicalStreamWriter writer) {
        writer.startElement(classMapper.mapNameToXML(field.getName()));

        Class actualType = obj.getClass();

        Class defaultType = classMapper.lookupDefaultType(field.getType());
        if (!actualType.equals(defaultType)) {
            writer.addAttribute(classAttributeIdentifier, classMapper.lookupName(actualType));
        }

        context.convertAnother(obj);

        writer.endElement();
    }

    public Object fromXML(UnmarshallingContext context) {
        Object result = context.currentObject();

        if (result == null) {
            result = objectFactory.create(context.getRequiredType());
        }

        while (context.xmlNextChild()) {
            String fieldName = classMapper.mapNameFromXML(context.xmlElementName());
            Iterator fields = getFields(result.getClass());
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
            String classAttribute = context.xmlAttribute(classAttributeIdentifier);
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

            context.xmlPop();
        }
        return result;
    }

    private Iterator getFields(Class cls) {
        List result = new LinkedList();

        while (!Object.class.equals(cls)) {
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                int modifiers = field.getModifiers();
                if (field.getName().startsWith("this$")) {
                    continue;
                }
                if (Modifier.isFinal(modifiers) ||
                        Modifier.isStatic(modifiers) ||
                        Modifier.isTransient(modifiers)) {
                    continue;
                }
                result.add(field);
            }
            cls = cls.getSuperclass();
        }

        return result.iterator();
    }

}
