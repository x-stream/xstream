/*
 * Copyright (C) 2011, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class BEAStaxReaderTest extends AbstractXMLReaderTest {

    private final HierarchicalStreamDriver driver = new BEAStaxDriver();

    // factory method
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return driver.createReader(new StringReader(xml));
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        // Implementation ignores XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES set to false.
        // super.testIsXXEVulnerableWithExternalParameterEntity();
    }

    // inherits tests from superclass
}
