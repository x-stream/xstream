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

import javax.xml.stream.XMLOutputFactory;

import com.ctc.wstx.stax.WstxOutputFactory;


public final class WstxWriterTest extends AbstractStaxWriterTest {
    @Override
    protected void assertXmlProducedIs(String expected) {
        if (!staxDriver.isRepairingNamespace() || expected.matches("<\\w+:\\w+ xmlns:\\w+=.+")) {
            expected = expected.replaceAll(" xmlns=\"\"", "");
        }
        expected = expected.replace("&#x0D;", "&#xd;");
        expected = expected.replace("&gt;", ">"); // unusual behavior in Woodstox, but allowed in spec
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    @Override
    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='UTF-8'?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        return new WstxOutputFactory();
    }

    @Override
    protected StaxDriver getStaxDriver() {
        return new WstxDriver();
    }
}
