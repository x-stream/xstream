/*
 * Copyright (C) 2012, 2013, 2018, 2025 XStream Committers.
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
    public final static Class PersistentBag = loadHibernateType("org.hibernate.collection.spi.PersistentBag");
    public final static Class PersistentList = loadHibernateType("org.hibernate.collection.spi.PersistentList");
    public final static Class PersistentMap = loadHibernateType("org.hibernate.collection.spi.PersistentMap");
    public final static Class PersistentSet = loadHibernateType("org.hibernate.collection.spi.PersistentSet");
    public final static Class PersistentSortedMap = loadHibernateType("org.hibernate.collection.spi.PersistentSortedMap");
    public final static Class PersistentSortedSet = loadHibernateType("org.hibernate.collection.spi.PersistentSortedSet");
    public final static Class EnversList = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.ListProxy");
    public final static Class EnversMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.MapProxy");
    public final static Class EnversSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SetProxy");
    public final static Class EnversSortedMap = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedMapProxy");
    public final static Class EnversSortedSet = loadHibernateEnversType("org.hibernate.envers.entities.mapper.relation.lazy.proxy.SortedSetProxy");

    private static Class loadHibernateType(String name) {
        Class type = null;
        try {
            try {
                try {
                    type = HibernateProxy.class.getClassLoader().loadClass(name);
                } catch (ClassNotFoundException e) {
                    // test Hibernate version 5.x
                    type = HibernateProxy.class.getClassLoader().loadClass(
                        name.replaceFirst("\\.spi\\.", ".internal."));
                }
            } catch (ClassNotFoundException e) {
                // test Hibernate version 3.x
                type = HibernateProxy.class.getClassLoader().loadClass(
                    name.replaceFirst("\\.spi\\.", "."));
            }
        } catch (ClassNotFoundException e) {
            // not available
        }
        return type;
    }

    private static Class loadHibernateEnversType(String name) {
        Class type = null;
        if (JVM.isVersion(5)) {
            try {
                type = HibernateProxy.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException e) {
                // not available
            }
        }
        return type;
    }
}
