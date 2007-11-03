/*
 * Copyright (C) 2007 Joerg Schaible
 * Created on 03.11.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.ctc.wstx.stax.WstxOutputFactory;

import junit.framework.Test;

import javax.xml.stream.XMLOutputFactory;

public final class WoodstoxStaxWriterTest extends AbstractStaxWriterTest {
    public WoodstoxStaxWriterTest() {
        System.setProperty(XMLOutputFactory.class.getName(), WstxOutputFactory.class
            .getName());
    }

    public static Test suite() {
        return createSuite(WoodstoxStaxWriterTest.class, WstxOutputFactory.class.getName());
    }

    protected void assertXmlProducedIs(String expected) {
        if (outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES).equals(Boolean.FALSE)) {
            expected = perlUtil.substitute("s#<(\\w+|\\w+:\\w+) (xmlns[^\"]*\"[^\"]*\")>#<$1>#g", expected);
        } else if(perlUtil.match("#<\\w+:\\w+(>| xmlns:\\w+=)#", expected)) {
            expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
        }
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2 />#g", expected);
        expected = replaceAll(expected, "&#x0D;", "&#xd;");
        expected = replaceAll(expected, "&gt;", ">"); // Woodstox bug !!
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='UTF-8'?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        return new WstxOutputFactory();
    }
}