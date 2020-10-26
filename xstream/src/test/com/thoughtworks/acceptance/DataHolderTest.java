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

package com.thoughtworks.acceptance;

import java.io.StringReader;
import java.io.StringWriter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;


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
        final String result = xstream
            .<String>unmarshal(DefaultDriver.create().createReader(new StringReader(xml)), null, dataHolder);

        // verify
        assertEquals("something", result);
        assertEquals("yes", dataHolder.get("saw-this"));
    }
}
