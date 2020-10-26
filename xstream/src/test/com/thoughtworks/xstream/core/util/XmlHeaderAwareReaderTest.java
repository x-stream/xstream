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
        final ByteArrayInputStream in = new ByteArrayInputStream("\n<!-- no header --><html/>".getBytes("us-ascii"));
        try (LineNumberReader reader = new LineNumberReader(new XmlHeaderAwareReader(in))) {
            assertEquals("", reader.readLine());
            assertEquals("<!-- no header --><html/>", reader.readLine());
        }
    }

    public void testDefaultValues() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("\n<?xml ?>".getBytes("us-ascii"));
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in)) {
            assertEquals(1.0, reader.getVersion(), 0.001);
            assertEquals(new InputStreamReader(in, "utf-8").getEncoding(), reader.getEncoding());
        }
    }

    public void testEvaluatesVersion() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("\n<?xml foo=\"bar\" version='1.1' ?>".getBytes(
            "us-ascii"));
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in)) {
            assertEquals(1.1, reader.getVersion(), 0.001);
        }
    }

    public void testEvaluatesEncoding() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("<?xml encoding=\"iso-8859-15\" ?>".getBytes(
            "us-ascii"));
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in)) {
            assertEquals(new InputStreamReader(in, "iso-8859-15").getEncoding(), reader.getEncoding());
        }
    }

    public void testValueEscaping() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream("<?xml version='1.\\1' ?>".getBytes("us-ascii"));
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in)) {
            assertEquals(1.1, reader.getVersion(), 0.001);
        }
    }

    public void testCanHandleImproperSizedPushbackInputStream() throws IOException {
        final InputStream in = new ByteArrayInputStream("<?xml version='1.\\1' ?>".getBytes("us-ascii"));
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(new PushbackInputStream(in, 1))) {
            assertEquals(1.1, reader.getVersion(), 0.001);
        }
    }

    public void testSkipsUtf8BOM() throws IOException {
        final byte[] bytes = "<?xml encoding=\"utf-8\" ?>".getBytes("us-ascii");
        final byte[] inBytes = new byte[bytes.length + 3];
        inBytes[0] = (byte)0xEF;
        inBytes[1] = (byte)0xBB;
        inBytes[2] = (byte)0xBF;
        System.arraycopy(bytes, 0, inBytes, 3, bytes.length);
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try (XmlHeaderAwareReader reader = new XmlHeaderAwareReader(in)) {
            assertEquals(new InputStreamReader(in, "utf-8").getEncoding(), reader.getEncoding());
        }
    }
}
