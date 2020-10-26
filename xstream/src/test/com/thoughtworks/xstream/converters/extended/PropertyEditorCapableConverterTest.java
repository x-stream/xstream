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

package com.thoughtworks.xstream.converters.extended;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class PropertyEditorCapableConverterTest extends TestCase {

    public static class SoftwarePropertyEditor extends PropertyEditorSupport {

        @Override
        public String getAsText() {
            final Software software = (Software)getValue();
            return software.vendor + ":" + software.name;
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            final int idx = text.indexOf(':');
            setValue(new Software(text.substring(0, idx), text.substring(idx + 1)));
        }

    }

    public void testCanBeUsedForCustomTypes() {
        final Software software = new Software("Joe Walnes", "XStream");
        final SingleValueConverter converter = new PropertyEditorCapableConverter(SoftwarePropertyEditor.class,
            Software.class);
        assertTrue(converter.canConvert(Software.class));
        assertEquals("Joe Walnes:XStream", converter.toString(software));
        assertEquals(software, converter.fromString("Joe Walnes:XStream"));
    }

    public void testConcurrentConversion() throws InterruptedException {
        final SingleValueConverter converter = new PropertyEditorCapableConverter(SoftwarePropertyEditor.class,
            Software.class);

        final Map<Throwable, String> exceptions = new HashMap<Throwable, String>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final Map<String, Software> references = new HashMap<String, Software>();
        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; ++i) {
            final String name = "JUnit Thread:" + i;
            references.put(name, new Software("JUnit Thread", Integer.toString(i)));
            threads[i] = new Thread(tg, name) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Software software = references.get(Thread.currentThread().getName());
                        while (i < 1000 && !interrupted()) {
                            final String formatted = converter.toString(software);
                            Thread.yield();
                            assertEquals(software, converter.fromString(formatted));
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

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have made at least 1 conversion", counter[0] >= threads.length);
    }

}
