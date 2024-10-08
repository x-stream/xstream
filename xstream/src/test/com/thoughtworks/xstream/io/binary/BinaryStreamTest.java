/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2015, 2016, 2018, 2019, 2021, 2024 XStream Committers.
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
import java.io.InputStream;
import java.io.StringReader;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.AbstractReaderTest;
import com.thoughtworks.xstream.io.xml.MXParserDriver;
import com.thoughtworks.xstream.security.InputManipulationException;


public class BinaryStreamTest extends AbstractReaderTest {

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
        final HierarchicalStreamReader xmlReader = new MXParserDriver().createReader(new StringReader(xml));

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

    @SuppressWarnings("resource")
    public void testHandleMaliciousInputsOfIdMappingTokens() {
        // Insert two successive id mapping tokens into the stream
        final byte[] byteArray = new byte[8];
        byteArray[0] = byteArray[4] = 10;
        byteArray[1] = byteArray[5] = -127;

        final InputStream in = new ByteArrayInputStream(byteArray);
        try {
            new BinaryStreamReader(in);
            fail("Thrown " + InputManipulationException.class.getName() + " expected");
        } catch (final InputManipulationException e) {
        }
    }
}
