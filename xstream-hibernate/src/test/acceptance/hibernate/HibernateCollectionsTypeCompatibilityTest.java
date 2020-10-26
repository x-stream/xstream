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
        assertXmlEquals(new ArrayList<Object>(), newHibernateCollection(Hibernate.PersistentBag, Collections
            .emptyList()));
    }

    public void testPersistentList() {
        assertXmlEquals(new ArrayList<Object>(), newHibernateCollection(Hibernate.PersistentList, Collections
            .emptyList()));
    }

    public void testPersistentMap() {
        assertXmlEquals(new HashMap<Object, Object>(), newHibernateCollection(Hibernate.PersistentMap, Collections
            .emptyMap()));
    }

    public void testPersistentSet() {
        assertXmlEquals(new HashSet<Object>(), newHibernateCollection(Hibernate.PersistentSet, Collections.emptySet()));
    }

    public void testPersistentSortedMap() {
        assertXmlEquals(new TreeMap<Object, Object>(), newHibernateCollection(Hibernate.PersistentSortedMap,
            new TreeMap<Object, Object>()));
    }

    public void testPersistentSortedSet() {
        assertXmlEquals(new TreeSet<Object>(), newHibernateCollection(Hibernate.PersistentSortedSet,
            new TreeSet<Object>()));
    }

    private Object newHibernateCollection(final Class<?> type, final Object secondArg) {
        Object instance = null;
        final Constructor<?>[] ctors = type.getConstructors();
        for (final Constructor<?> ctor : ctors) {
            if (ctor.getParameterTypes().length == 2) {
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
