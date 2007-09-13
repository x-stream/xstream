/*
 * Copyright (C) 2007 XStream committers.
 * Created on 13.09.2007 by Joerg Schaible.
 */
package com.thoughtworks.xstream.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class XmlHeaderAwareReaderTest extends TestCase {

    public void testKeepsAllBytesInStream() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream("\n<!-- no header --><html/>".getBytes("us-ascii"));
        LineNumberReader reader = new LineNumberReader(new XmlHeaderAwareReader(in));
        assertEquals("", reader.readLine());
        assertEquals("<!-- no header --><html/>", reader.readLine());
    }

    public void testDefaultValues() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream("\n<?xml ?>".getBytes("us-ascii"));
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in);
        assertEquals(1.0, reader.getVersion(), 0.001);
        assertEquals(new InputStreamReader(in, "utf-8").getEncoding(), reader.getEncoding());
    }

    public void testEvaluatesVersion() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream("\n<?xml foo=\"bar\" version='1.1' ?>".getBytes("us-ascii"));
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in);
        assertEquals(1.1, reader.getVersion(), 0.001);
    }

    public void testEvaluatesEncoding() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream("<?xml encoding=\"iso-8859-15\" ?>".getBytes("us-ascii"));
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in);
        assertEquals(new InputStreamReader(in, "iso-8859-15").getEncoding(), reader.getEncoding());
    }

    public void testValueEscaping() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream("<?xml version='1.\\1' ?>".getBytes("us-ascii"));
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in);
        assertEquals(1.1, reader.getVersion(), 0.001);
    }
}
