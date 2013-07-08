/*
 * Copyright (C) 2008, 2009, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.cache.products;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Uses XStream with a CachingMapper caching the aliasForAttribute method.
 *
 * @author J&ouml;rg Schaible
 */
public class DefaultImplementationCache extends XStreamCache {

    protected List getMappers() {
        List list = super.getMappers();
        list.add(CachingMapper.class);
        return list;
    }

    public String toString() {
        return "Default Implementation Cache";
    }
    
    public static class CachingMapper extends MapperWrapper {

        private transient Map defaultImplementationCache;

        public CachingMapper(Mapper wrapped) {
            super(wrapped);
            readResolve();
        }

        public Class defaultImplementationOf(Class type) {
            WeakReference reference = (WeakReference) defaultImplementationCache.get(type);
            if (reference != null) {
                Class cached = (Class) reference.get();
                if (cached != null) {
                    return cached;
                }
            }
            
            Class result = super.defaultImplementationOf(type);
            defaultImplementationCache.put(type, new WeakReference(result));
            return result;
        }

        private Object readResolve() {
            defaultImplementationCache = Collections.synchronizedMap(new WeakHashMap(128));
            return this;
        }

    }
}
