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
