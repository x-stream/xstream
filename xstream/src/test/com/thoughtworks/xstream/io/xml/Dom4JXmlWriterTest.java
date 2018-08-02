/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 05. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringWriter;

import org.dom4j.io.OutputFormat;


public class Dom4JXmlWriterTest extends AbstractXMLWriterTest {

    private StringWriter out;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final Dom4JDriver driver = new Dom4JDriver();

        final OutputFormat format = OutputFormat.createCompactFormat();
        format.setTrimText(false);
        format.setSuppressDeclaration(true);
        driver.setOutputFormat(format);

        out = new StringWriter();
        writer = driver.createWriter(out);
    }

    @Override
    protected void assertXmlProducedIs(String expected) {
        writer.close();
        expected = replaceAll(expected, "&#xd;", "\r");
        // attributes are not properly escaped
        expected = replaceAll(expected, "&#xa;", "\n");
        expected = replaceAll(expected, "&#x9;", "\t");
        assertEquals(expected, out.toString());
    }
}
