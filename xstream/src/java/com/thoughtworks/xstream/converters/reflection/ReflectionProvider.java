package com.thoughtworks.xstream.converters.reflection;



/**
 * Provides core reflection services.
 */
public interface ReflectionProvider {

    Object newInstance(Class type);

    void eachSerializableField(Class type, Block visitor);

    Object readField(Object object, String fieldName);
    void writeField(Object object, String fieldName, Object value);
    Class getFieldType(Object object, String fieldName);

    interface Block {
        void visit(String name, Class type);
    }
}
