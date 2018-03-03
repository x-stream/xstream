/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 04. October 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.StringReader;
import java.io.StringWriter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;


public class DataHolderTest extends AbstractAcceptanceTest {

    static class StringWithPrefixConverter implements Converter {

        @Override
        public boolean canConvert(final Class<?> type) {
            return type == String.class;
        }

        @Override
        public void marshal(final Object source, final HierarchicalStreamWriter writer,
                final MarshallingContext context) {
            final String prefix = (String)context.get("prefix");
            if (prefix != null) {
                writer.addAttribute("prefix", prefix);
            }
            writer.setValue(source.toString());
        }

        @Override
        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
            context.put("saw-this", reader.getAttribute("can-you-see-me"));
            return reader.getValue();
        }

    }

    @SuppressWarnings("resource")
    public void testCanBePassedInToMarshallerExternally() {
        // setup
        xstream.registerConverter(new StringWithPrefixConverter());
        final StringWriter writer = new StringWriter();
        final DataHolder dataHolder = xstream.newDataHolder();

        // execute
        dataHolder.put("prefix", "additional stuff");
        xstream.marshal("something", new PrettyPrintWriter(writer), dataHolder);

        // verify
        final String expected = "<string prefix=\"additional stuff\">something</string>";
        assertEquals(expected, writer.toString());
    }

    public void testCanBePassedInToUnmarshallerExternally() {
        // setup
        xstream.registerConverter(new StringWithPrefixConverter());
        final DataHolder dataHolder = xstream.newDataHolder();

        // execute
        final String xml = "<string can-you-see-me=\"yes\">something</string>";
        final String result = xstream.<String>unmarshal(new XppDriver().createReader(new StringReader(xml)), null,
            dataHolder);

        // verify
        assertEquals("something", result);
        assertEquals("yes", dataHolder.get("saw-this"));
    }
}
