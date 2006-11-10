package com.thoughtworks.xstream.io.binary;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.io.xml.XppReader;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

public class BinaryStreamTest extends AbstractXMLReaderTest {

    private HierarchicalStreamCopier copier = new HierarchicalStreamCopier();

    protected void setUp() throws Exception {
        super.setUp();
    }

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        // Transmogrify XML input into binary format.
        HierarchicalStreamReader xmlReader = new XppReader(new StringReader(xml));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HierarchicalStreamWriter binaryWriter = new BinaryStreamWriter(buffer);
        copier.copy(xmlReader, binaryWriter);

        return new BinaryStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
    }

    public void testHandlesMoreThan256Ids() {
        int count = 500;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HierarchicalStreamWriter binaryWriter = new BinaryStreamWriter(buffer);
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

        HierarchicalStreamReader binaryReader
                = new BinaryStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
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
