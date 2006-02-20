package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;

public class TreeMarshallerTest extends AbstractAcceptanceTest {

    static class Thing {
        Thing thing;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.NO_REFERENCES);
    }

    public void testThrowsExceptionWhenDetectingCircularReferences() {
        Thing a = new Thing();
        Thing b = new Thing();
        a.thing = b;
        b.thing = a;

        try {
            xstream.toXML(a);
            fail("expected exception");
        } catch (TreeMarshaller.CircularReferenceException expected) {
            // good
        }
    }
}
