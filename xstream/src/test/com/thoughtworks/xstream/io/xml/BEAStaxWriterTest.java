/*
 * Copyright (C) 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.bea.xml.stream.XMLOutputFactoryBase;

import junit.framework.Test;

import javax.xml.stream.XMLOutputFactory;

public final class BEAStaxWriterTest extends AbstractStaxWriterTest {
    public BEAStaxWriterTest() {
        System.setProperty(XMLOutputFactory.class.getName(), XMLOutputFactoryBase.class
            .getName());
    }

    public static Test suite() {
        return createSuite(BEAStaxWriterTest.class, XMLOutputFactoryBase.class.getName());
    }

    protected void assertXmlProducedIs(String expected) {
        expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#xd;", "&#13;");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='utf-8'?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        return new XMLOutputFactoryBase();
    }
}