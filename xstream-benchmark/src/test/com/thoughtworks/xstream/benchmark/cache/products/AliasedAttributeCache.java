/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.cache.products;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Uses XStream with a CachingMapper caching the aliasForAttribute method.
 *
 * @author J&ouml;rg Schaible
 */
public class AliasedAttributeCache extends XStreamCache {

    protected Mapper createCachingMapper(Mapper mapper) {
        return new CachingMapper(mapper);
    }

    public String toString() {
        return "Aliased Attribute Cache";
    }
    
    public static class CachingMapper extends MapperWrapper {

//        private transient Map realClassCache;
//        private transient Map serializedClassCache;
        private transient Map attributeAliasCache;
//        private transient Map defaultImplementationCache;

        public CachingMapper(Mapper wrapped) {
            super(wrapped);
            readResolve();
        }

//        public Class realClass(String elementName) {
//            WeakReference reference = (WeakReference) realClassCache.get(elementName);
//            if (reference != null) {
//                Class cached = (Class) reference.get();
//                if (cached != null) {
//                    return cached;
//                }
//            }
//            
//            Class result = super.realClass(elementName);
//            realClassCache.put(elementName, new WeakReference(result));
//            return result;
//        }

//        public String serializedClass(Class type) {
//            String alias = (String) serializedClassCache.get(type);
//            if (alias != null) {
//                return alias;
//            }
//            
//            String result = super.serializedClass(type);
//            serializedClassCache.put(type, alias);
//            return result;
//        }

        public String aliasForAttribute(String attribute) {
            String alias = (String) attributeAliasCache.get(attribute);
            if (alias != null) {
                return alias;
            }
            
            String result = super.aliasForAttribute(attribute);
            attributeAliasCache.put(attribute, alias);
            return result;
        }

//        public Class defaultImplementationOf(Class type) {
//            WeakReference reference = (WeakReference) defaultImplementationCache.get(type);
//            if (reference != null) {
//                Class cached = (Class) reference.get();
//                if (cached != null) {
//                    return cached;
//                }
//            }
//            
//            Class result = super.defaultImplementationOf(type);
//            defaultImplementationCache.put(type, new WeakReference(result));
//            return result;
//        }

        private Object readResolve() {
//            realClassCache = Collections.synchronizedMap(new HashMap(128));
//            serializedClassCache = Collections.synchronizedMap(new WeakHashMap(128));
            attributeAliasCache = Collections.synchronizedMap(new HashMap(256));
//            defaultImplementationCache = Collections.synchronizedMap(new WeakHashMap(128));
            return this;
        }

    }
}
