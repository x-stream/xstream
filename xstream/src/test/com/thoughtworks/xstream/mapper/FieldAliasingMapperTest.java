/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.Software;

public class FieldAliasingMapperTest extends AbstractAcceptanceTest {

    public void testAllowsIndividualFieldsToBeAliased() {
        Software in = new Software("ms", "word");
        xstream.alias("software", Software.class);
        xstream.aliasField("CUSTOM-VENDOR", Software.class, "vendor");
        xstream.aliasField("CUSTOM-NAME", Software.class, "name");

        String expectedXml = "" +
                "<software>\n" +
                "  <CUSTOM-VENDOR>ms</CUSTOM-VENDOR>\n" +
                "  <CUSTOM-NAME>word</CUSTOM-NAME>\n" +
                "</software>";

        assertBothWays(in, expectedXml);
    }
}
