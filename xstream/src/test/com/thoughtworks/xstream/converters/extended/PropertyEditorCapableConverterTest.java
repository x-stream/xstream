/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import junit.framework.TestCase;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;


/**
 * @author J&ouml;rg Schaible
 */
public class PropertyEditorCapableConverterTest extends TestCase {

    public static class SoftwarePropertyEditor extends PropertyEditorSupport {

        public String getAsText() {
            Software software = (Software)getValue();
            return software.vendor + ":" + software.name;
        }

        public void setAsText(String text) throws IllegalArgumentException {
            int idx = text.indexOf(':');
            setValue(new Software(text.substring(0, idx), text.substring(idx + 1)));
        }

    }

    public void testCanBeUsedForCustomTypes() {
        Software software = new Software("Joe Walnes", "XStream");
        SingleValueConverter converter = new PropertyEditorCapableConverter(
            SoftwarePropertyEditor.class, Software.class);
        assertTrue(converter.canConvert(Software.class));
        assertEquals("Joe Walnes:XStream", converter.toString(software));
        assertEquals(software, converter.fromString("Joe Walnes:XStream"));
    }

    public void testConcurrentConversion() throws InterruptedException {
        final SingleValueConverter converter = new PropertyEditorCapableConverter(
            SoftwarePropertyEditor.class, Software.class);

        final Map exceptions = new HashMap();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            public void uncaughtException(Thread t, Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final Map references = new HashMap();
        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; ++i) {
            final String name = "JUnit Thread:" + i;
            references.put(name, new Software("JUnit Thread", Integer.toString(i)));
            threads[i] = new Thread(tg, name) {

                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Software software = (Software)references.get(Thread
                            .currentThread()
                            .getName());
                        while (i < 1000 && !interrupted()) {
                            String formatted = converter.toString(software);
                            Thread.yield();
                            assertEquals(software, converter.fromString(formatted));
                            ++i;
                        }
                    } catch (InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    synchronized (counter) {
                        counter[0] += i;
                    }
                }

            };
        }

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].start();
                threads[i].wait();
            }
        }

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].notifyAll();
            }
        }

        Thread.sleep(1000);

        for (int i = 0; i < threads.length; ++i) {
            threads[i].interrupt();
        }
        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue(
            "Each thread should have made at least 1 conversion", counter[0] >= threads.length);
    }

}
