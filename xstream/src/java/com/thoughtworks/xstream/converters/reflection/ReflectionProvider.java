package com.thoughtworks.xstream.converters.reflection;

import java.util.Iterator;

/**
 * Provides core reflection services.
 */
public interface ReflectionProvider {

    Object newInstance(Class type);

    Iterator listSerializableFields(Class type);

    void eachSerializableFields(Class type, Block visitor);

    Object readField(Object object, String fieldName);

    interface Block {
        void visit(String name, Class type);
    }
}
