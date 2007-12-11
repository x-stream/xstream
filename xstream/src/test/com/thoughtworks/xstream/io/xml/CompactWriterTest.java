/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.QuickWriter;

import java.io.StringWriter;
import java.io.Writer;

public class CompactWriterTest extends AbstractXMLWriterTest {
    private Writer buffer;

    protected void setUp() throws Exception {
        super.setUp();
        buffer = new StringWriter();
        writer = new CompactWriter(buffer);
    }

    protected void assertXmlProducedIs(String expected) {
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

        String expected = "<hello><world><one>potato</one><two>potatae</two></world></hello>";
        assertXmlProducedIs(expected);
    }

    public void testEncodesFunnyXmlChars() {
        writer.startNode("tag");
        writer.setValue("hello & this isn't \"really\" <good>");
        writer.endNode();

        String expected = "<tag>hello &amp; this isn&apos;t &quot;really&quot; &lt;good&gt;</tag>";

        assertXmlProducedIs(expected);
    }

    public void testWriteTextAsCDATA() {
        writer = new CompactWriter(buffer) {
            protected void writeText(QuickWriter writer, String text) {
                writer.write("<[CDATA[");
                writer.write(text);
                writer.write("]]>");
            }
        };

        writer.startNode("tag");
        writer.setValue("hello & this isn't \"really\" <good>");
        writer.endNode();

        String expected = "<tag><[CDATA[hello & this isn't \"really\" <good>]]></tag>";

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

        String expected = "" +
                "<tag hello=\"world\">" +
                "<inner foo=\"bar\" poo=\"par\">hi</inner>" +
                "</tag>";

        assertXmlProducedIs(expected);
    }
}
