package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class TreeMarshallerTest extends AbstractAcceptanceTest {

    class Thing {
        Thing thing;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMarshallingStrategy(new TreeMarshallingStrategy());
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
