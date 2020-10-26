/*
 * Copyright (C) 2006, 2007, 2009, 2014  XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. October 2006 by Joerg Schaible
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
