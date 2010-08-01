/*
 * Copyright (C) 2007, 2008, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackInputStream;

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
    
    public void testCanHandleImproperSizedPushbackInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream("<?xml version='1.\\1' ?>".getBytes("us-ascii"));
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(new PushbackInputStream(in, 1));
        assertEquals(1.1, reader.getVersion(), 0.001);
    }

    public void testSkipsUtf8BOM() throws IOException {
        byte[] bytes = "<?xml encoding=\"utf-8\" ?>".getBytes("us-ascii");
        byte[] inBytes = new byte[bytes.length+3];
        inBytes[0] = (byte)0xEF;
        inBytes[1] = (byte)0xBB;
        inBytes[2] = (byte)0xBF;
        System.arraycopy(bytes, 0, inBytes, 3, bytes.length);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in);
        assertEquals(new InputStreamReader(in, "utf-8").getEncoding(), reader.getEncoding());
    }
}
