/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. October 2011 by Joerg Schaible
 */

package acceptance.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentMap;
import org.hibernate.collection.PersistentSet;
import org.hibernate.collection.PersistentSortedMap;
import org.hibernate.collection.PersistentSortedSet;


/**
 * @author J&ouml;rg Schaible
 */
public class HibernateCollectionsTypeCompatibilityTest extends AbstractHibernateAcceptanceTest {

    public void testPersistentBag() {
        assertXmlEquals(new ArrayList(), new PersistentBag(null, Collections.EMPTY_LIST));
    }

    public void testPersistentList() {
        assertXmlEquals(new ArrayList(), new PersistentList(null, Collections.EMPTY_LIST));
    }

    public void testPersistentMap() {
        assertXmlEquals(new HashMap(), new PersistentMap(null, Collections.EMPTY_MAP));
    }

    public void testPersistentSet() {
        assertXmlEquals(new HashSet(), new PersistentSet(null, Collections.EMPTY_SET));
    }

    public void testPersistentSortedMap() {
        assertXmlEquals(new TreeMap(), new PersistentSortedMap(null, new TreeMap()));
    }

    public void testPersistentSortedSet() {
        assertXmlEquals(new TreeSet(), new PersistentSortedSet(null, new TreeSet()));
    }

    private void assertXmlEquals(Object reference, Object hibernateCollection) {
        final String expectedXml = xstream.toXML(reference);
        final String loadedXml = xstream.toXML(hibernateCollection);
        assertEquals(expectedXml, loadedXml);
    }
}
