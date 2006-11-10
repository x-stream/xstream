package com.thoughtworks.xstream.io.copy;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.XppReader;

import java.io.StringReader;
import java.io.StringWriter;

public class HierarchicalStreamCopierTest extends AbstractXMLReaderTest {

    private HierarchicalStreamCopier copier = new HierarchicalStreamCopier();

    // This test leverages the existing (comprehensive) tests for the XML readers
    // and adds an additional stage of copying in.

    // factory method - overriding base class.
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        HierarchicalStreamReader sourceReader = new XppReader(new StringReader(xml));

        StringWriter buffer = new StringWriter();
        HierarchicalStreamWriter destinationWriter = new CompactWriter(buffer);

        copier.copy(sourceReader, destinationWriter);

        return new XppReader(new StringReader(buffer.toString()));
    }

    public void testSkipsValueIfEmpty() {
        String input = "<root><empty1/><empty2></empty2><not-empty>blah</not-empty></root>";
        String expected = "<root><empty1/><empty2/><not-empty>blah</not-empty></root>";
        HierarchicalStreamReader sourceReader = new XppReader(new StringReader(input));

        StringWriter buffer = new StringWriter();
        HierarchicalStreamWriter destinationWriter = new CompactWriter(buffer);

        copier.copy(sourceReader, destinationWriter);

        assertEquals(expected, buffer.toString());
    }

}
