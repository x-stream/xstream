/*
 * Copyright (C) 2011, 2012, 2018, 2025 XStream Committers.
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
        assertXmlEquals(new ArrayList<>(), newHibernateCollection(Hibernate.PersistentBag, Collections
            .emptyList()));
    }

    public void testPersistentList() {
        assertXmlEquals(new ArrayList<>(), newHibernateCollection(Hibernate.PersistentList, Collections
            .emptyList()));
    }

    public void testPersistentMap() {
        assertXmlEquals(new HashMap<>(), newHibernateCollection(Hibernate.PersistentMap, Collections
            .emptyMap()));
    }

    public void testPersistentSet() {
        assertXmlEquals(new HashSet<>(), newHibernateCollection(Hibernate.PersistentSet, Collections.emptySet()));
    }

    public void testPersistentSortedMap() {
        assertXmlEquals(new TreeMap<>(), newHibernateCollection(Hibernate.PersistentSortedMap,
            new TreeMap<>()));
    }

    public void testPersistentSortedSet() {
        assertXmlEquals(new TreeSet<>(), newHibernateCollection(Hibernate.PersistentSortedSet,
            new TreeSet<>()));
    }

    private Object newHibernateCollection(final Class<?> type, final Object secondArg) {
        Object instance = null;
        final Constructor<?>[] ctors = type.getConstructors();
        for (final Constructor<?> ctor : ctors) {
            if (ctor.getParameterTypes().length == 2
                && ctor.getParameterTypes()[1].isAssignableFrom(secondArg.getClass())) {
                try {
                    instance = ctor.newInstance(null, secondArg);
                } catch (final InstantiationException e) {
                    e.printStackTrace();
                } catch (final IllegalAccessException e) {
                    e.printStackTrace();
                } catch (final InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        assertNotNull(instance);
        return instance;
    }

    private void assertXmlEquals(final Object reference, final Object hibernateCollection) {
        final String expectedXml = xstream.toXML(reference);
        final String loadedXml = xstream.toXML(hibernateCollection);
        assertEquals(expectedXml, loadedXml);
    }
}
