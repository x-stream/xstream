/*
 * Copyright (C) 2011, 2012 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. October 2011 by Joerg Schaible
 */

package acceptance.hibernate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.xstream.hibernate.util.Hibernate;


/**
 * @author J&ouml;rg Schaible
 */
public class HibernateCollectionsTypeCompatibilityTest extends AbstractHibernateAcceptanceTest {

    public void testPersistentBag() {
        assertXmlEquals(new ArrayList(), newHibernateCollection(Hibernate.PersistentBag, Collections.EMPTY_LIST));
    }

    public void testPersistentList() {
        assertXmlEquals(new ArrayList(), newHibernateCollection(Hibernate.PersistentList, Collections.EMPTY_LIST));
    }

    public void testPersistentMap() {
        assertXmlEquals(new HashMap(), newHibernateCollection(Hibernate.PersistentMap, Collections.EMPTY_MAP));
    }

    public void testPersistentSet() {
        assertXmlEquals(new HashSet(), newHibernateCollection(Hibernate.PersistentSet, Collections.EMPTY_SET));
    }

    public void testPersistentSortedMap() {
        assertXmlEquals(new TreeMap(), newHibernateCollection(Hibernate.PersistentSortedMap, new TreeMap()));
    }

    public void testPersistentSortedSet() {
        assertXmlEquals(new TreeSet(), newHibernateCollection(Hibernate.PersistentSortedSet, new TreeSet()));
    }
    
    private Object newHibernateCollection(Class type, Object secondArg) {
        Object instance = null;
        Constructor[] ctors = type.getConstructors();
        for(int i = 0; i < ctors.length; ++i) {
            if (ctors[i].getParameterTypes().length == 2) {
                try {
                    instance = ctors[i].newInstance(new Object[]{null, secondArg});
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        assertNotNull(instance);
        return instance;
    }

    private void assertXmlEquals(Object reference, Object hibernateCollection) {
        final String expectedXml = xstream.toXML(reference);
        final String loadedXml = xstream.toXML(hibernateCollection);
        assertEquals(expectedXml, loadedXml);
    }
}
