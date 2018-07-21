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
package com.thoughtworks.xstream.io.copy;

import java.io.StringReader;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;


public class HierarchicalStreamCopierTest extends AbstractXMLReaderTest {

    private final HierarchicalStreamCopier copier = new HierarchicalStreamCopier();

    // This test leverages the existing (comprehensive) tests for the XML readers
    // and adds an additional stage of copying in.

    // factory method - overriding base class.
    @SuppressWarnings("resource")
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final HierarchicalStreamReader sourceReader = new Xpp3Driver().createReader(new StringReader(xml));

        final StringWriter buffer = new StringWriter();
        final HierarchicalStreamWriter destinationWriter = new CompactWriter(buffer);

        copier.copy(sourceReader, destinationWriter);

        return new XppReader(new StringReader(buffer.toString()), XppFactory.createDefaultParser());
    }

    public void testSkipsValueIfEmpty() throws XmlPullParserException {
        final String input = "<root><empty1/><empty2></empty2><not-empty>blah</not-empty></root>";
        final String expected = "<root><empty1/><empty2/><not-empty>blah</not-empty></root>";
        final StringWriter buffer = new StringWriter();
        try (final HierarchicalStreamReader sourceReader = new XppReader(new StringReader(input), XppFactory
            .createDefaultParser()); 
                final HierarchicalStreamWriter destinationWriter = new CompactWriter(buffer)) {

            copier.copy(sourceReader, destinationWriter);
        }

        assertEquals(expected, buffer.toString());
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
