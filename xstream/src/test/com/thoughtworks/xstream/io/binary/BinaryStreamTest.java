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

package com.thoughtworks.xstream.io.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.AbstractReaderTest;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;


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
}
