/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


public class JDomAcceptanceTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("x", X.class);
        xstream.allowTypes(X.class);
    }

    public void testUnmarshalsObjectFromJDOM() throws Exception {
        final String xml = "<x>"
            + "  <aStr>joe</aStr>"
            + "  <anInt>8</anInt>"
            + "  <innerObj>"
            + "    <yField>walnes</yField>"
            + "  </innerObj>"
            + "</x>";

        final Document doc = new SAXBuilder().build(new StringReader(xml));

        try (final JDomReader reader = new JDomReader(doc)) {
            final X x = xstream.<X>unmarshal(reader);

            assertEquals("joe", x.aStr);
            assertEquals(8, x.anInt);
            assertEquals("walnes", x.innerObj.yField);
        }
    }

    public void testMarshalsObjectToJDOM() {
        final X x = new X();
        x.anInt = 9;
        x.aStr = "zzz";
        x.innerObj = new Y();
        x.innerObj.yField = "ooo";

        final String expected = "<x>\n"
            + "  <aStr>zzz</aStr>\n"
            + "  <anInt>9</anInt>\n"
            + "  <innerObj>\n"
            + "    <yField>ooo</yField>\n"
            + "  </innerObj>\n"
            + "</x>";

        try (final JDomWriter writer = new JDomWriter()) {
            xstream.marshal(x, writer);
            final List<Element> result = writer.getTopLevelNodes();

            assertEquals("Result list should contain exactly 1 element", 1, result.size());

            final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n"));

            assertEquals(expected, outputter.outputString(result));
        }
    }
}
