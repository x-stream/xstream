/*
 * Copyright (C) 2006, 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;


public class ReferenceByIDMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testIgnoresImplicitCollection() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList wl = new WithNamedList("foo");
        wl.things.add("Hello");
        wl.things.add("Daniel");

        final String expected = "" 
                + "<strings id=\"1\">\n"
                + "  <string>Hello</string>\n"
                + "  <string>Daniel</string>\n"
                + "  <name>foo</name>\n"
                + "</strings>";

        assertBothWays(wl, expected);
    }

    static class List { 
        public Object o; 
        public ArrayList list = new ArrayList(); 
    } 
    
    public void testIgnoresImplicitCollectionAtAnyFieldPosition() {
        final List another = new List(); 
        another.o = new Object(); 
        another.list.add(new Object()); 
        xstream.addImplicitCollection(List.class, "list"); 
        xstream.alias("list", List.class);
        
        final String expected = "" 
            + "<list id=\"1\">\n"
            + "  <o id=\"2\"/>\n"
            + "  <object id=\"3\"/>\n"
            + "</list>";
        
        assertBothWays(another, expected);
    }
}
