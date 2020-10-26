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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;


/**
 * @author J&ouml;rg Schaible
 */
public class StaxDriverTest extends AbstractAcceptanceTest {
    private static class MyStaxDriver extends StaxDriver {
        public boolean createStaxWriterCalled = false;
        public boolean createStaxReaderCalled = false;

        @Override
        public StaxWriter createStaxWriter(final XMLStreamWriter out) throws StreamException {
            createStaxWriterCalled = true;
            try {
                return super.createStaxWriter(out);
            } catch (final XMLStreamException e) {
                throw new StreamException(e);
            }
        }

        @Override
        public AbstractPullReader createStaxReader(final XMLStreamReader in) {
            createStaxReaderCalled = true;
            return super.createStaxReader(in);
        }
    }

    public void testCanOverloadStaxReaderAndWriterInstantiation() {
        System.setProperty(XMLInputFactory.class.getName(), MXParserFactory.class.getName());
        System.setProperty(XMLOutputFactory.class.getName(), XMLOutputFactoryBase.class.getName());
        final MyStaxDriver driver = new MyStaxDriver();
        xstream = new XStream(driver);
        assertBothWays("Hi", "<?xml version='1.0' encoding='utf-8'?><string>Hi</string>");
        assertTrue(driver.createStaxReaderCalled);
        assertTrue(driver.createStaxWriterCalled);
    }
}
