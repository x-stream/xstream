package com.thoughtworks.xstream.converters.reflection;

/**
 * Provides core reflection services.
 */
public interface ReflectionProvider {

    Object newInstance(Class type);

    void readSerializableFields(Object object, Block visitor);

    void writeField(Object object, String fieldName, Object value);

    void writeField(Object object, String fieldName, Object value, Class definedIn);

    Class getFieldType(Object object, String fieldName);

    interface Block {
        void visit(String name, Class type, Class definedIn, Object value);
    }
}
