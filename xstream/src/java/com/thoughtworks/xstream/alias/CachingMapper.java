package com.thoughtworks.xstream.alias;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class CachingMapper extends ClassMapperWrapper {

    private final Map cache = Collections.synchronizedMap(new HashMap());

    public CachingMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public Class lookupType(String elementName) {
        final String key = elementName;
        Class cached = (Class) cache.get(key);
        if (cached != null) {
            return cached;
        } else {
            Class result = super.lookupType(elementName);
            cache.put(key, result);
            return result;
        }
    }

}
