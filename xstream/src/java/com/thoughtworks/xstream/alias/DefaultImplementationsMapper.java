package com.thoughtworks.xstream.alias;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class DefaultImplementationsMapper extends ClassMapperWrapper {

    private final Map baseTypeToDefaultTypeMap = Collections.synchronizedMap(new HashMap());

    public DefaultImplementationsMapper(ClassMapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    protected void addDefaults() {
        // register primitive types
        baseTypeToDefaultTypeMap.put(boolean.class, Boolean.class);
        baseTypeToDefaultTypeMap.put(char.class, Character.class);
        baseTypeToDefaultTypeMap.put(int.class, Integer.class);
        baseTypeToDefaultTypeMap.put(float.class, Float.class);
        baseTypeToDefaultTypeMap.put(double.class, Double.class);
        baseTypeToDefaultTypeMap.put(short.class, Short.class);
        baseTypeToDefaultTypeMap.put(byte.class, Byte.class);
        baseTypeToDefaultTypeMap.put(long.class, Long.class);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        super.alias(elementName, type, defaultImplementation);
        baseTypeToDefaultTypeMap.put(type, defaultImplementation);
    }

    public Class defaultImplementationOf(Class type) {
        Class result = (Class) baseTypeToDefaultTypeMap.get(type);
        return result == null ? super.defaultImplementationOf(type) : result;
    }

    public Class lookupDefaultType(Class baseType) {
        return defaultImplementationOf(baseType);
    }

}
