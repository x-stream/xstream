/*
 * Copyright (C) 2012, 2013 Joerg Schaible.
 * All rights reserved.
 *
 * Created on 08.06.2012 by Joerg Schaible
 */
package com.thoughtworks.xstream.hibernate.util;

import com.thoughtworks.xstream.core.JVM;

import org.hibernate.proxy.HibernateProxy;


/**
 * Utility class for Hibernate support.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.3
 */
public class Hibernate {
    public final static Class PersistentBag = loadHibernateType("org.hibernate.collection.internal.PersistentBag");
    public final static Class PersistentList = loadHibernateType("org.hibernate.collection.internal.PersistentList");
    public final static Class PersistentMap = loadHibernateType("org.hibernate.collection.internal.PersistentMap");
    public final static Class PersistentSet = loadHibernateType("org.hibernate.collection.internal.PersistentSet");
    public final static Class PersistentSortedMap = loadHibernateType("org.hibernate.collection.internal.PersistentSortedMap");
    public final static Class PersistentSortedSet = loadHibernateType("org.hibernate.collection.internal.PersistentSortedSet");
    public final static Class EnversList = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.ListProxy");
    public final static Class EnversMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.MapProxy");
    public final static Class EnversSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SetProxy");
    public final static Class EnversSortedMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedMapProxy");
    public final static Class EnversSortedSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedSetProxy");

    private static Class loadHibernateType(String name) {
        Class type = null;
        try {
            try {
                type = HibernateProxy.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException e) {
                type = HibernateProxy.class.getClassLoader().loadClass(
                    name.replaceFirst("\\.internal\\.", "."));
            }
        } catch (ClassNotFoundException e) {
            // not available
        }
        return type;
    }

    private static Class loadHibernateEnversType(String name) {
        Class type = null;
        if (JVM.is15()) {
            try {
                type = HibernateProxy.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException e) {
                // not available
            }
        }
        return type;
    }
}
