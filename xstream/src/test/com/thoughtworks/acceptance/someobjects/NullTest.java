package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class NullTest extends AbstractAcceptanceTest {
    
    public void testNull() {
        String expected = "<null/>";

        String xml = xstream.toXML(null);

        assertEquals(expected, xml);
        assertNull(xstream.fromXML(xml));
    }
}
