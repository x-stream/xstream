/*
 * Copyright (C) 2008, 2009, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.cache.products;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Uses XStream with the CachingMapper of 1.2.2.
 *
 * @author J&ouml;rg Schaible
 */
public class Cache122 extends XStreamCache {

    protected List getMappers() {
        List list = super.getMappers();
        Object cglibMapper = list.remove(1);
        Object dynProxyMapper = null;
        if (JVM.loadClassForName("net.sf.cglib.proxy.Enhancer") != null) {
            dynProxyMapper = list.remove(1);
        }
        int idx = list.indexOf(ImplicitCollectionMapper.class);
        if (JVM.loadClassForName("net.sf.cglib.proxy.Enhancer") != null) {
            list.add(idx+1, dynProxyMapper);
        }
        list.add(idx+1, cglibMapper);
        list.add(CachingMapper.class);
        return list;
    }

    public String toString() {
        return "XStream 1.2.2 Cache";
    }
    
    public static class CachingMapper extends MapperWrapper {

        private transient Map realClassCache;

        public CachingMapper(Mapper wrapped) {
            super(wrapped);
            realClassCache = Collections.synchronizedMap(new HashMap());
        }

        public Class realClass(String elementName) {
            Class cached = (Class)realClassCache.get(elementName);
            if (cached != null) {
                return cached;
            }
            
            Class result = super.realClass(elementName);
            realClassCache.put(elementName, result);
            return result;
        }

        private Object readResolve() {
            realClassCache = Collections.synchronizedMap(new HashMap());
            return this;
        }

    }
}
