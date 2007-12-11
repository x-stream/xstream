/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

public class JDomAcceptanceTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("x", X.class);
    }

    public void testUnmarshalsObjectFromJDOM() throws Exception {
        String xml =
                "<x>" +
                "  <aStr>joe</aStr>" +
                "  <anInt>8</anInt>" +
                "  <innerObj>" +
                "    <yField>walnes</yField>" +
                "  </innerObj>" +
                "</x>";

        Document doc = new SAXBuilder().build(new StringReader(xml));

        X x = (X) xstream.unmarshal(new JDomReader(doc));

        assertEquals("joe", x.aStr);
        assertEquals(8, x.anInt);
        assertEquals("walnes", x.innerObj.yField);
    }

    public void testMarshalsObjectToJDOM() {
        X x = new X();
        x.anInt = 9;
        x.aStr = "zzz";
        x.innerObj = new Y();
        x.innerObj.yField = "ooo";

        String expected =
                "<x>\n" +
                "  <aStr>zzz</aStr>\n" +
                "  <anInt>9</anInt>\n" +
                "  <innerObj>\n" +
                "    <yField>ooo</yField>\n" +
                "  </innerObj>\n" +
                "</x>";

        JDomWriter writer = new JDomWriter();
        xstream.marshal(x, writer);
        List result = writer.getTopLevelNodes();

        assertEquals("Result list should contain exactly 1 element",
                                                        1, result.size());

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setLineSeparator("\n"));

        assertEquals(expected, outputter.outputString(result));
    }
}

