package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class IDNestedCircularReferenceTest extends AbstractNestedCircularReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

}
