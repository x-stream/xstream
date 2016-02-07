/*
 * Copyright (C) 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 7. February 2016 by Aaron Johnson
 */
package com.thoughtworks.xstream.converters.extended;

import java.io.File;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class FileConverterTest extends AbstractAcceptanceTest {

    public void testConvertsToSingleString() {

        assertBothWays(
                new File("../a/relative/path"),
                "<file>../a/relative/path</file>");

        assertBothWays(
                new File("/an/absolute/path"),
                "<file>/an/absolute/path</file>");
    }

}
