package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.DefaultClassMapper;
import com.thoughtworks.xstream.core.DefaultNameMapper;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import junit.framework.TestCase;

import java.lang.reflect.Array;

public abstract class AbstractAcceptanceTest extends TestCase {

    protected XStream xstream = new XStream(new Sun14ReflectionProvider(), new DefaultClassMapper(new DefaultNameMapper()), new XppDriver());

    protected void assertBothWays(Object root, String xml) {
        String resultXml = xstream.toXML(root);
        assertEquals(xml, resultXml);
        Object resultRoot = xstream.fromXML(resultXml);
        compareObjects(root, resultRoot);
    }

    private void compareObjects(Object expected, Object actual) {
        if (actual.getClass().isArray()) {
            assertArrayEquals(expected, actual);
        } else {
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(expected, actual);
        }
    }

    protected void assertArrayEquals(Object expected, Object actual) {
        assertEquals(Array.getLength(expected), Array.getLength(actual));
        for (int i = 0; i < Array.getLength(expected); i++) {
            assertEquals(Array.get(expected, i), Array.get(actual, i));
        }
    }
}
