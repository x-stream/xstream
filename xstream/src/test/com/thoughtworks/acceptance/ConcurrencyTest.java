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

package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.WithNamedList;


/**
 * @author J&ouml;rg Schaible
 */
public class ConcurrencyTest extends AbstractAcceptanceTest {

    public void testConcurrentXStreaming() throws InterruptedException {
        xstream.alias("thing", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");

        final List<String> reference = new ArrayList<>(Arrays.asList(new String[]{"A", "B", "C", "D"}));
        @SuppressWarnings("unchecked")
        final WithNamedList<Object>[] namedLists = new WithNamedList[5];
        for (int i = 0; i < namedLists.length; ++i) {
            namedLists[i] = new WithNamedList<Object>("Name " + (i + 1));
            namedLists[i].things.add(new Software("walnes", "XStream 1." + i));
            namedLists[i].things.add(reference);
            namedLists[i].things.add(new RuntimeException("JUnit " + i)); // a Serializable
        }

        final Map<Throwable, String> exceptions = new HashMap<>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final Object object = new ArrayList<>(Arrays.asList(namedLists));
        final String xml = xstream.toXML(object);
        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        while (!interrupted()) {
                            assertBothWays(object, xml);
                            ++i;
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    synchronized (counter) {
                        counter[0] += i;
                    }
                }

            };
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.start();
                thread.wait();
            }
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }

        Thread.sleep(1000);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions has been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have made at least 1 conversion", counter[0] >= threads.length);
    }
}
