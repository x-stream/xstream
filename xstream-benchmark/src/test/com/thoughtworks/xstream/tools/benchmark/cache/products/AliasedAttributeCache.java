/*
 * Copyright (C) 2008, 2009, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.cache.products;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Uses XStream with a CachingMapper caching the aliasForAttribute method.
 *
 * @author J&ouml;rg Schaible
 */
public class AliasedAttributeCache extends XStreamCache {

    protected List getMappers() {
        List list = super.getMappers();
        list.add(CachingMapper.class);
        return list;
    }

    public String toString() {
        return "Aliased Attribute Cache";
    }
    
    public static class CachingMapper extends MapperWrapper {

        private transient Map attributeAliasCache;

        public CachingMapper(Mapper wrapped) {
            super(wrapped);
            readResolve();
        }

        public String aliasForAttribute(String attribute) {
            String alias = (String) attributeAliasCache.get(attribute);
            if (alias != null) {
                return alias;
            }
            
            String result = super.aliasForAttribute(attribute);
            attributeAliasCache.put(attribute, alias);
            return result;
        }

        private Object readResolve() {
            attributeAliasCache = Collections.synchronizedMap(new HashMap(256));
            return this;
        }

    }
}
