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
package com.thoughtworks.acceptance;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.XStream;


/**
 * @author Aaron Johnson
 * @author J&ouml;rg Schaible
 */
public class Extended17TypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setupSecurity(XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypeHierarchy(Path.class);
    }

    /**
     * @author Aaron Johnson
     */
    public void testPath() {
        assertBothWays(Paths.get("../a/relative/path"), "<path>../a/relative/path</path>");
        assertBothWays(Paths.get("/an/absolute/path"), "<path>/an/absolute/path</path>");
    }
}
