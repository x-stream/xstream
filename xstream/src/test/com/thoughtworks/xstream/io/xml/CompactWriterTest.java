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

package com.thoughtworks.xstream.io.xml;

import java.io.StringWriter;
import java.io.Writer;

import com.thoughtworks.xstream.core.util.QuickWriter;


public class CompactWriterTest extends AbstractXMLWriterTest {
    private Writer buffer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        buffer = new StringWriter();
        writer = new CompactWriter(buffer);
    }

    @Override
    protected void assertXmlProducedIs(final String expected) {
        assertEquals(expected, buffer.toString());
    }

    public void testXmlIsIndented() {
        writer.startNode("hello");
        writer.startNode("world");

        writer.startNode("one");
        writer.setValue("potato");
        writer.endNode();

        writer.startNode("two");
        writer.setValue("potatae");
        writer.endNode();

        writer.endNode();
        writer.endNode();

        final String expected = "<hello><world><one>potato</one><two>potatae</two></world></hello>";
        assertXmlProducedIs(expected);
    }

    public void testEncodesFunnyXmlChars() {
        writer.startNode("tag");
        writer.setValue("hello & this isn't \"really\" <good>");
        writer.endNode();

        final String expected = "<tag>hello &amp; this isn&apos;t &quot;really&quot; &lt;good&gt;</tag>";

        assertXmlProducedIs(expected);
    }

    public void testWriteTextAsCDATA() {
        writer = new CompactWriter(buffer) {
            @Override
            protected void writeText(final QuickWriter writer, final String text) {
                writer.write("<![CDATA[");
                writer.write(text);
                writer.write("]]>");
            }
        };

        writer.startNode("tag");
        writer.setValue("hello & this isn't \"really\" <good>");
        writer.endNode();

        final String expected = "<tag><![CDATA[hello & this isn't \"really\" <good>]]></tag>";

        assertXmlProducedIs(expected);
    }

    public void testAttributesCanBeWritten() {
        writer.startNode("tag");
        writer.addAttribute("hello", "world");
        writer.startNode("inner");
        writer.addAttribute("foo", "bar");
        writer.addAttribute("poo", "par");
        writer.setValue("hi");
        writer.endNode();
        writer.endNode();

        final String expected = "" + "<tag hello=\"world\">" + "<inner foo=\"bar\" poo=\"par\">hi</inner>" + "</tag>";

        assertXmlProducedIs(expected);
    }
}
