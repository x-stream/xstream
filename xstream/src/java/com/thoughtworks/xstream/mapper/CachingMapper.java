package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper that caches which names map to which classes. Prevents repetitive searching and class loading.
 *
 * @author Joe Walnes
 */
public class CachingMapper extends MapperWrapper {

    private transient Map cache = Collections.synchronizedMap(new HashMap());

    public CachingMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #CachingMapper(Mapper)}
     */
    public CachingMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public Class realClass(String elementName) {
        Class cached = (Class) cache.get(elementName);
        if (cached != null) {
            return cached;
        } else {
            Class result = super.realClass(elementName);
            cache.put(elementName, result);
            return result;
        }
    }
    
    private Object readResolve() {
        cache = Collections.synchronizedMap(new HashMap());
        return this;
    }

}
