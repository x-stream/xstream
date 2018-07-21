/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2015, 2016, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 04. June 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.io.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;


public class BinaryStreamTest extends AbstractXMLReaderTest {

    private final HierarchicalStreamCopier copier = new HierarchicalStreamCopier();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    // factory method
    @SuppressWarnings("resource")
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        // Transmogrify XML input into binary format.
        final HierarchicalStreamReader xmlReader = new Xpp3Driver().createReader(new StringReader(xml));

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final HierarchicalStreamWriter binaryWriter = new BinaryStreamWriter(buffer);
        copier.copy(xmlReader, binaryWriter);

        return new BinaryStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
    }

    public void testHandlesMoreThan256Ids() {
        final int count = 500;

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (final HierarchicalStreamWriter binaryWriter = new BinaryStreamWriter(buffer)) {
            binaryWriter.startNode("root");
            for (int i = 0; i < count; i++) {
                binaryWriter.startNode("node" + i);
                binaryWriter.endNode();
            }
            for (int i = 0; i < count; i++) {
                binaryWriter.startNode("node" + i);
                binaryWriter.endNode();
            }
            binaryWriter.endNode();
        }

        try (final HierarchicalStreamReader binaryReader = new BinaryStreamReader(new ByteArrayInputStream(buffer
            .toByteArray()))) {
            assertEquals("root", binaryReader.getNodeName());
            for (int i = 0; i < count; i++) {
                assertTrue("Expected child " + i, binaryReader.hasMoreChildren());
                binaryReader.moveDown();
                assertEquals("node" + i, binaryReader.getNodeName());
                binaryReader.moveUp();
            }
            for (int i = 0; i < count; i++) {
                assertTrue("Expected child " + i, binaryReader.hasMoreChildren());
                binaryReader.moveDown();
                assertEquals("node" + i, binaryReader.getNodeName());
                binaryReader.moveUp();
            }
        }
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("resolve entity")) {
                throw e;
            }
        }
    }

}
