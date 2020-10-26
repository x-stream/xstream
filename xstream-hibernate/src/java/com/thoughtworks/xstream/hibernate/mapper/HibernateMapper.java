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

package com.thoughtworks.xstream.hibernate.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.hibernate.util.Hibernate;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * Mapper for Hibernate types. It will map the class names of the Hibernate collections and Envers collection proxies
 * with equivalents of the JDK at serialization time. It will also map the names of the proxy types to the names of the
 * proxies element's type.
 *
 * @author Konstantin Pribluda
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class HibernateMapper extends MapperWrapper {

    final private Map<Class<?>, Class<?>> collectionMap = new HashMap<>();

    public HibernateMapper(final MapperWrapper mapper) {
        super(mapper);
        collectionMap.put(Hibernate.PersistentBag, ArrayList.class);
        collectionMap.put(Hibernate.PersistentList, ArrayList.class);
        collectionMap.put(Hibernate.PersistentMap, HashMap.class);
        collectionMap.put(Hibernate.PersistentSet, HashSet.class);
        collectionMap.put(Hibernate.PersistentSortedMap, TreeMap.class);
        collectionMap.put(Hibernate.PersistentSortedSet, TreeSet.class);
        collectionMap.put(Hibernate.EnversList, ArrayList.class);
        collectionMap.put(Hibernate.EnversMap, HashMap.class);
        collectionMap.put(Hibernate.EnversSet, HashSet.class);
        collectionMap.put(Hibernate.EnversSortedMap, TreeMap.class);
        collectionMap.put(Hibernate.EnversSortedSet, TreeSet.class);
        collectionMap.remove(null);
    }

    @Override
    public Class<?> defaultImplementationOf(final Class<?> clazz) {
        if (collectionMap.containsKey(clazz)) {
            return super.defaultImplementationOf(collectionMap.get(clazz));
        }

        return super.defaultImplementationOf(clazz);
    }

    @Override
    public String serializedClass(final Class<?> clazz) {
        if (clazz != null) {
            if (collectionMap.containsKey(clazz)) {
                // Pretend this is the underlying collection class and map that instead
                return super.serializedClass(collectionMap.get(clazz));
            }
            // check whether we are Hibernate proxy and substitute real name
            if (HibernateProxy.class.isAssignableFrom(clazz)) {
                return super.serializedClass(clazz.getSuperclass());
            }
        }
        return super.serializedClass(clazz);
    }
}
