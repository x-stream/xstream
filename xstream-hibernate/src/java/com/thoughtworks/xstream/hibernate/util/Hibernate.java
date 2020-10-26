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

package com.thoughtworks.xstream.hibernate.util;

import org.hibernate.proxy.HibernateProxy;


/**
 * Utility class for Hibernate support.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.3
 */
public class Hibernate {
    /** <code>PersistentBag</code> contains Hibernate's PersistenBag class type. */
    public final static Class<?> PersistentBag = loadHibernateType("org.hibernate.collection.internal.PersistentBag");
    /** <code>PersistentList</code> contains Hibernate's PersistenList class type. */
    public final static Class<?> PersistentList = loadHibernateType("org.hibernate.collection.internal.PersistentList");
    /** <code>PersistentMap</code> contains Hibernate's PersistenMap class type. */
    public final static Class<?> PersistentMap = loadHibernateType("org.hibernate.collection.internal.PersistentMap");
    /** <code>PersistentSet</code> contains Hibernate's PersistenSet class type. */
    public final static Class<?> PersistentSet = loadHibernateType("org.hibernate.collection.internal.PersistentSet");
    /** <code>PersistentSortedMap</code> contains Hibernate's PersistenSortedMap class type. */
    public final static Class<?> PersistentSortedMap = loadHibernateType("org.hibernate.collection.internal.PersistentSortedMap");
    /** <code>PersistentSortedSet</code> contains Hibernate's PersistenSortedSet class type. */
    public final static Class<?> PersistentSortedSet = loadHibernateType("org.hibernate.collection.internal.PersistentSortedSet");
    /** <code>EnversList</code> contains the ListProxy class type for Hibernate Envers. */
    public final static Class<?> EnversList = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.ListProxy");
    /** <code>EnversMap</code> contains the MapProxy class type for Hibernate Envers. */
    public final static Class<?> EnversMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.MapProxy");
    /** <code>EnversSet</code> contains the SetProxy class type for Hibernate Envers. */
    public final static Class<?> EnversSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SetProxy");
    /** <code>EnversSortedMap</code> contains the SortedMapProxy class type for Hibernate Envers. */
    public final static Class<?> EnversSortedMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedMapProxy");
    /** <code>EnversSortedSet</code> contains the SortedSetProxy class type for Hibernate Envers. */
    public final static Class<?> EnversSortedSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedSetProxy");

    private static Class<?> loadHibernateType(final String name) {
        Class<?> type = null;
        try {
            try {
                type = HibernateProxy.class.getClassLoader().loadClass(name);
            } catch (final ClassNotFoundException e) {
                type = HibernateProxy.class.getClassLoader().loadClass(name.replaceFirst("\\.internal\\.", "."));
            }
        } catch (final ClassNotFoundException e) {
            // not available
        }
        return type;
    }

    private static Class<?> loadHibernateEnversType(final String name) {
        Class<?> type = null;
        try {
            type = HibernateProxy.class.getClassLoader().loadClass(name);
        } catch (final ClassNotFoundException e) {
            // not available
        }
        return type;
    }
}
