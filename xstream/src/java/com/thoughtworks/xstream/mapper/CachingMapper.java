/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
