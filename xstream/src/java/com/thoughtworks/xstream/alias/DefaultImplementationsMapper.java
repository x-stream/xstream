package com.thoughtworks.xstream.alias;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class DefaultImplementationsMapper extends ClassMapperWrapper {

    private final Map typeToImpl = Collections.synchronizedMap(new HashMap());
    private final Map implToType = Collections.synchronizedMap(new HashMap());

    public DefaultImplementationsMapper(ClassMapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    protected void addDefaults() {
        // register primitive types
        addDefaultImplementation(boolean.class, Boolean.class);
        addDefaultImplementation(char.class, Character.class);
        addDefaultImplementation(int.class, Integer.class);
        addDefaultImplementation(float.class, Float.class);
        addDefaultImplementation(double.class, Double.class);
        addDefaultImplementation(short.class, Short.class);
        addDefaultImplementation(byte.class, Byte.class);
        addDefaultImplementation(long.class, Long.class);
    }

    public void addDefaultImplementation(Class type, Class defaultImplementation) {
        typeToImpl.put(type, defaultImplementation);
        implToType.put(defaultImplementation, type);
    }

    public String lookupName(Class type) {
        Class baseType = (Class) implToType.get(type);
        return baseType == null ? super.lookupName(type) : super.lookupName(baseType);
    }

    public Class defaultImplementationOf(Class type) {
        Class result = (Class) typeToImpl.get(type);
        return result == null ? super.defaultImplementationOf(type) : result;
    }

}
