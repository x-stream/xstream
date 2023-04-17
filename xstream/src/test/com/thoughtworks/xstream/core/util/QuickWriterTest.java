/*
 * Copyright (C) 2009, 2018, 2023 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. September 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.io.StringWriter;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class QuickWriterTest extends TestCase {

    public void testUnbuffered() {
        final StringWriter stringWriter = new StringWriter();
        try (QuickWriter writer = new QuickWriter(stringWriter, 0)) {
            writer.write("Joe");
            assertEquals(stringWriter.toString(), "Joe");
            writer.write(' ');
            assertEquals(stringWriter.toString(), "Joe ");
            writer.write("Walnes".toCharArray());
            assertEquals(stringWriter.toString(), "Joe Walnes");
        }
    }

    public void testBufferingChar() {
        final StringWriter stringWriter = new StringWriter();
        try (QuickWriter writer = new QuickWriter(stringWriter, 1024)) {
            final char[] filler = new char[1023];
            writer.write(filler);
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            writer.write(' ');
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            writer.write(' ');
            assertEquals("flushed", 1024, stringWriter.getBuffer().length());
        }
    }

    public void testBufferingCharArray() {
        final StringWriter stringWriter = new StringWriter();
        try (QuickWriter writer = new QuickWriter(stringWriter, 1024)) {
            final char[] filler = new char[1023];
            writer.write(filler);
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            final char[] one = {' '};
            writer.write(one);
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            writer.write(one);
            assertEquals("flushed", 1024, stringWriter.getBuffer().length());
        }
    }

    public void testBufferingString() {
        final StringWriter stringWriter = new StringWriter();
        try (QuickWriter writer = new QuickWriter(stringWriter, 1024)) {
            final char[] filler = new char[1023];
            writer.write(filler);
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            writer.write(" ");
            assertEquals("not flushed yet", 0, stringWriter.getBuffer().length());
            writer.write(" ");
            assertEquals("flushed", 1024, stringWriter.getBuffer().length());
        }
    }
}
