/*
 * Copyright (C) 2012 Joerg Schaible.
 * All rights reserved.
 *
 * Created on 08.06.2012 by Joerg Schaible
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
    public final static Class PersistentBag = loadHibernateType("org.hibernate.collection.internal.PersistentBag");
    public final static Class PersistentList = loadHibernateType("org.hibernate.collection.internal.PersistentList");
    public final static Class PersistentMap = loadHibernateType("org.hibernate.collection.internal.PersistentMap");
    public final static Class PersistentSet = loadHibernateType("org.hibernate.collection.internal.PersistentSet");
    public final static Class PersistentSortedMap = loadHibernateType("org.hibernate.collection.internal.PersistentSortedMap");
    public final static Class PersistentSortedSet = loadHibernateType("org.hibernate.collection.internal.PersistentSortedSet");

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
}
