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

package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;


public class ObjectIdDictionaryTest extends TestCase {

    public void testMapsIdsToObjectReferences() {
        final ObjectIdDictionary<String> dict = new ObjectIdDictionary<String>();
        final Object a = new Object();
        final Object b = new Object();
        final Object c = new Object();
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        dict.associateId(c, "id c");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
        assertEquals("id c", dict.lookupId(c));
    }

    public void testTreatsObjectsThatAreEqualButNotSameInstanceAsDifferentReference() {
        final ObjectIdDictionary<String> dict = new ObjectIdDictionary<String>();
        final Integer a = new Integer(3);
        final Integer b = new Integer(3);
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
    }

    public void testEntriesAreGarbageCollected() throws InterruptedException {
        final ObjectIdDictionary<String> dict = new ObjectIdDictionary<String>();

        int counter = 0;
        for (; counter < 1000; ++counter) {
            final String s = new String("JUnit " + counter); // enforce new object
            assertFalse("Failed in (" + counter + ")", dict.containsId(s));
            dict.associateId(s, "X");
            if (counter % 50 == 49) {
                System.gc();
                Thread.sleep(10);
            }
        }
        final int size = dict.size();
        assertTrue("Dictionary did not shrink; " + counter + " distinct objects; " + size + " size", dict.size() < 250);
    }
}
