package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects
 * that can be constructed are limited.
 * <p/>
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default constructors.
 * Cannot newInstance: classes without public visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates the object.
 * </p>
 */
public class PureJavaReflectionProvider implements ReflectionProvider {

    protected FieldDictionary fieldDictionary = new FieldDictionary();

    public Object newInstance(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    public void visitSerializableFields(Object object, ReflectionProvider.Visitor visitor) {
        for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
            Field field = (Field) iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }
            validateFieldAccess(field);
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            }
            visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
        }
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
        validateFieldAccess(field);
        try {
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        }
    }

    public Class getFieldType(Object object, String fieldName, Class definedIn) {
        return fieldDictionary.field(object.getClass(), fieldName, definedIn).getType();
    }

    public boolean fieldDefinedInClass(String fieldName, Class type) {
        try {
            fieldDictionary.field(type, fieldName, null);
            return true;
        } catch (ObjectAccessException e) {
            return false;
        }
    }

    protected boolean fieldModifiersSupported(Field field) {
        return !(Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers()));
    }

    protected void validateFieldAccess(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            throw new ObjectAccessException("Invalid final field "
                    + field.getDeclaringClass().getName() + "." + field.getName());
        }
    }

}
