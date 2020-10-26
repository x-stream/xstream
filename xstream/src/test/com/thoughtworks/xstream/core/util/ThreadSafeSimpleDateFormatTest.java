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

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class ThreadSafeSimpleDateFormatTest extends TestCase {

    public void testDateFormatting() throws ParseException {
        final ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss,S z", TimeZone
            .getTimeZone("UTC"), 2, 4, false);
        final Date now = new Date();
        final String formatted = format.format(now);
        assertEquals(now, format.parse(formatted));
    }

    public void testConcurrentDateFormatting() throws InterruptedException {

        final ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss,S z", TimeZone
            .getTimeZone("UTC"), 2, 4, false);
        final Date now = new Date();

        final Map<Throwable, String> exceptions = new HashMap<Throwable, String>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[10];
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
                        while (i < 1000 && !interrupted()) {
                            final String formatted = format.format(now);
                            Thread.yield();
                            assertEquals(now, format.parse(formatted));
                            ++i;
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    } catch (final ParseException e) {
                        fail("Unexpected ParseException");
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

        Thread.sleep(1500);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have made at least 1 conversion", counter[0] >= threads.length);
    }

}
