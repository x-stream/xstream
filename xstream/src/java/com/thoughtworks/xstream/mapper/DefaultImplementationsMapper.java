package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper that resolves default implementations of classes. For example, mapper.lookupName(ArrayList.class) will return
 * java.util.List. Calling mapper.defaultImplementationOf(List.class) will return ArrayList.
 *
 * @author Joe Walnes 
 */
public class DefaultImplementationsMapper extends MapperWrapper {

    private final Map typeToImpl = Collections.synchronizedMap(new HashMap());
    private final Map implToType = Collections.synchronizedMap(new HashMap());

    public DefaultImplementationsMapper(ClassMapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    protected void addDefaults() {
        // register primitive types
        addDefaultImplementation(Boolean.class, boolean.class);
        addDefaultImplementation(Character.class, char.class);
        addDefaultImplementation(Integer.class, int.class);
        addDefaultImplementation(Float.class, float.class);
        addDefaultImplementation(Double.class, double.class);
        addDefaultImplementation(Short.class, short.class);
        addDefaultImplementation(Byte.class, byte.class);
        addDefaultImplementation(Long.class, long.class);
    }

    public void addDefaultImplementation(Class defaultImplementation, Class ofType) {
        typeToImpl.put(ofType, defaultImplementation);
        implToType.put(defaultImplementation, ofType);
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
