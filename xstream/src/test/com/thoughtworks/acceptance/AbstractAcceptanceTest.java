package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

public abstract class AbstractAcceptanceTest extends TestCase {

    protected XStream xstream = new XStream();

    protected void assertBothWays(Object root, String xml) {
        String resultXml = xstream.toXML(root);
        assertEquals(xml, resultXml);
        Object resultRoot = xstream.fromXML(resultXml);
        assertEquals(root.getClass(), resultRoot.getClass());
        assertEquals(root, resultRoot);
    }
}
