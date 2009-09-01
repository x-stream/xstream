/*
 * Copyright (C) 2009 XStream Committers.
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
        StringWriter stringWriter = new StringWriter();
        QuickWriter writer = new QuickWriter(stringWriter, 0);
        writer.write("Joe");
        assertEquals(stringWriter.toString(), "Joe");
        writer.write(' ');
        assertEquals(stringWriter.toString(), "Joe ");
        writer.write("Walnes".toCharArray());
        assertEquals(stringWriter.toString(), "Joe Walnes");
    }
}
