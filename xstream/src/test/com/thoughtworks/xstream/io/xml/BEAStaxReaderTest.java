/*
 * Copyright (C) 2011, 2015, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

public class BEAStaxReaderTest extends AbstractStaxReaderTest {

    @Override
    protected StaxDriver createDriver(final QNameMap qnameMap) {
        return new BEAStaxDriver(qnameMap);
    }

    @Override
    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        // Implementation ignores XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES set to false.
        // super.testIsXXEVulnerableWithExternalParameterEntity();
    }

    // inherits tests from superclass
}
