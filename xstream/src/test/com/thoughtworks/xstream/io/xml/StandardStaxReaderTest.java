/*
 * Copyright (C) 2015, 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. September 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

public class StandardStaxReaderTest extends AbstractStaxReaderTest {

    @Override
    protected StaxDriver createDriver(final QNameMap qnameMap) {
        return new StandardStaxDriver(qnameMap);
    }

    // inherits tests from superclass
}
