package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper that resolves default implementations of classes. For example, mapper.lookupName(ArrayList.class) will return
 * java.util.List. Calling mapper.defaultImplementationOf(List.class) will return ArrayList.
 *
 * @author Joe Walnes 
 */
public class DefaultImplementationsMapper extends MapperWrapper {

    private final Map typeToImpl = new HashMap();
    private transient Map implToType = new HashMap();

    public DefaultImplementationsMapper(Mapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    /**
     * @deprecated As of 1.2, use {@link #DefaultImplementationsMapper(Mapper)}
     */
    public DefaultImplementationsMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    protected void addDefaults() {
        // null handling
        addDefaultImplementation(null, Mapper.Null.class);
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

    public String serializedClass(Class type) {
        Class baseType = (Class) implToType.get(type);
        return baseType == null ? super.serializedClass(type) : super.serializedClass(baseType);
    }

    public Class defaultImplementationOf(Class type) {
        if (typeToImpl.containsKey(type)) {
            return (Class)typeToImpl.get(type);
        } else {
            return super.defaultImplementationOf(type);
        }
    }
    
    private Object readResolve() {
        implToType = new HashMap();
        for (final Iterator iter = typeToImpl.keySet().iterator(); iter.hasNext();) {
            final Object type = iter.next();
            implToType.put(typeToImpl.get(type), type);
        }
        return this;
    }

}
