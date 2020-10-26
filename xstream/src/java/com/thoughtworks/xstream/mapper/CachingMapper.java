/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.security.ForbiddenClassException;


/**
 * Mapper that caches which names map to which classes. Prevents repetitive searching and class loading.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class CachingMapper extends MapperWrapper implements Caching {

    private transient ConcurrentMap<String, ? super Object> realClassCache;

    public CachingMapper(final Mapper wrapped) {
        super(wrapped);
        readResolve();
    }

    @Override
    public Class<?> realClass(final String elementName) {
        final Object cached = realClassCache.get(elementName);
        if (cached != null) {
            if (cached instanceof Class) {
                return (Class<?>)cached;
            }
            throw (XStreamException)cached;
        }

        try {
            realClassCache.putIfAbsent(elementName, super.realClass(elementName));
            return (Class<?>)realClassCache.get(elementName);
        } catch (final ForbiddenClassException | CannotResolveClassException e) {
            realClassCache.putIfAbsent(elementName, e);
            throw e;
        }
    }

    @Override
    public void flushCache() {
        realClassCache.clear();
    }

    private Object readResolve() {
        realClassCache = new ConcurrentHashMap<>(128);
        return this;
    }
}
