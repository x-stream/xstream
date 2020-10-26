/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.core;

import java.util.ArrayList;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.XStream;


public class ReferenceByIDMarshallingStrategyTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testIgnoresImplicitCollection() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        final WithNamedList<String> wl = new WithNamedList<>("foo");
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
        public ArrayList<Object> list = new ArrayList<Object>();
    }

    public void testIgnoresImplicitCollectionAtAnyFieldPosition() {
        final List another = new List();
        another.o = new Object();
        another.list.add(new Object());
        xstream.addImplicitCollection(List.class, "list");
        xstream.alias("list", List.class);

        final String expected = "" //
            + "<list id=\"1\">\n"
            + "  <o id=\"2\"/>\n"
            + "  <object id=\"3\"/>\n"
            + "</list>";

        assertBothWays(another, expected);
    }
}
