/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import javax.xml.stream.XMLOutputFactory;

public final class JDK6StaxWriterTest extends AbstractStaxWriterTest {
    final static String className = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";
    private final Class factoryClass;

    public static Test suite() {
        return createSuite(JDK6StaxWriterTest.class, className);
    }

    protected void assertXmlProducedIs(String expected) {
        if (outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES).equals(Boolean.FALSE)) {
            expected = perlUtil.substitute("s#<(\\w+|\\w+:\\w+) (xmlns[^\"]*\"[^\"]*\")>#<$1>#g", expected);
        }
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#xd;", "\r");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    public JDK6StaxWriterTest() {
        try {
            this.factoryClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new AssertionFailedError("Cannot load JDK 6 class " + className);
        }
        System.setProperty(XMLOutputFactory.class.getName(), className);
    }

    protected String getXMLHeader() {
        return "<?xml version=\"1.0\" ?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        try {
            return (XMLOutputFactory)this.factoryClass.newInstance();
        } catch (InstantiationException e) {
            throw new AssertionFailedError("Cannot instantiate " + className);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedError("Cannot access default ctor of " + className);
        }
    }
}