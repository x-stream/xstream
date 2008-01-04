/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.cache.products;

import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Uses XStream with the CachingMapper of 1.2.2.
 *
 * @author J&ouml;rg Schaible
 */
public class NoCache extends XStreamCache {

    protected Mapper createCachingMapper(Mapper mapper) {
        return mapper;
    }

    public String toString() {
        return "No Cache";
    }
}
