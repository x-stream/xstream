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
        expected = expected.replace("&#xd;", "\r");
        // attributes are not properly escaped
        expected = expected.replace("&#xa;", "\n");
        expected = expected.replace("&#x9;", "\t");
        assertEquals(expected, out.toString());
    }
}
