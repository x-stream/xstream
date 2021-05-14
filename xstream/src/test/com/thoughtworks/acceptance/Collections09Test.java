/*
 * Copyright (C) 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. May 2021 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.List;


public class Collections09Test extends AbstractAcceptanceTest {

    public void testListFromListOf() {
        xstream.allowTypes("java.util.CollSer");

        final List<String> list = List.of("hi", "bye");

        assertBothWays(list, ""//
            + "<java.util.ImmutableCollections_-List12 resolves-to=\"java.util.CollSer\" serialization=\"custom\">\n"
            + "  <java.util.CollSer>\n"
            + "    <default>\n"
            + "      <tag>1</tag>\n"
            + "    </default>\n"
            + "    <int>2</int>\n"
            + "    <string>hi</string>\n"
            + "    <string>bye</string>\n"
            + "  </java.util.CollSer>\n"
            + "</java.util.ImmutableCollections_-List12>");
    }
}
