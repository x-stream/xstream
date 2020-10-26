/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 25. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;


public class TreeMarshallerTest extends AbstractAcceptanceTest {

    static class Thing {
        Thing thing;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.NO_REFERENCES);
    }

    public void testThrowsExceptionWhenDetectingCircularReferences() {
        final Thing a = new Thing();
        final Thing b = new Thing();
        a.thing = b;
        b.thing = a;

        try {
            xstream.toXML(a);
            fail("expected exception");
        } catch (final TreeMarshaller.CircularReferenceException expected) {
            // good
        }
    }
}
