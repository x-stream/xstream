/*
 * Copyright (C) 2006, 2007, 2018, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. July 2006 by Joerg Schaible
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
        final String staxInput = System.getProperty(XMLInputFactory.class.getName());
        final String staxOutput = System.getProperty(XMLOutputFactory.class.getName());
        System.setProperty(XMLInputFactory.class.getName(), MXParserFactory.class.getName());
        System.setProperty(XMLOutputFactory.class.getName(), XMLOutputFactoryBase.class.getName());
        try {
            final MyStaxDriver driver = new MyStaxDriver();
            xstream = new XStream(driver);
            assertBothWays("Hi", "<?xml version='1.0' encoding='utf-8'?><string>Hi</string>");
            assertTrue(driver.createStaxReaderCalled);
            assertTrue(driver.createStaxWriterCalled);
        } finally {
            if (staxInput != null) {
                System.setProperty(XMLInputFactory.class.getName(), staxInput);
	    } else {
                System.clearProperty(XMLInputFactory.class.getName());
            }
            if (staxOutput != null) {
                System.setProperty(XMLOutputFactory.class.getName(), staxOutput);
	    } else {
                System.clearProperty(XMLOutputFactory.class.getName());
            }
        }
    }
}
