/*
 * Copyright (C) 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. June 2012 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


public class JDom2AcceptanceTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.allowTypes(X.class, Y.class);
        xstream.alias("x", X.class);
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

        try (final JDom2Reader reader = new JDom2Reader(doc)) {
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

        try (final JDom2Writer writer = new JDom2Writer()) {
            xstream.marshal(x, writer);
            final List<Element> result = writer.getTopLevelNodes();

            assertEquals("Result list should contain exactly 1 element", 1, result.size());

            final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n"));

            assertEquals(expected, outputter.outputString(result));
        }
    }
}
