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

package com.thoughtworks.xstream.io.copy;

import java.io.StringReader;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractReaderTest;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;


public class HierarchicalStreamCopierTest extends AbstractReaderTest {

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
}
