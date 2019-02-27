/*
 * Copyright (C) 2011, 2015, 2016, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.XStreamException;


public class WstxReaderTest extends AbstractStaxReaderTest {

    @Override
    protected StaxDriver createDriver(final QNameMap qnameMap) {
        return new WstxDriver(qnameMap);
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("external entity")) {
                throw e;
            }
        }
    }

    @Override
    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalParameterEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("external entity")) {
                throw e;
            }
        }
    }

    // inherits tests from superclass
}
