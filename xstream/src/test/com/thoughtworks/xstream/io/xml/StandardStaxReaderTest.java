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

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class StandardStaxReaderTest extends AbstractStaxReaderTest {

    @Override
    protected StaxDriver createDriver(final QNameMap qnameMap) {
        return new StandardStaxDriver(qnameMap);
    }

    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final String prefix = getName().endsWith("ISOControlCharactersInValue") ? XML_1_1_HEADER : "";
        return super.createReader(prefix + xml);
    }

    @Override
    protected String getSpecialCharsInJavaNamesForXml10() {
        return super.getSpecialCharsInJavaNamesForXml10_4th();
    }

    @Override
    public void testNullCharacterInValue() throws Exception {
        // not possible, null value is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInValue() throws Exception {
        // not possible, character is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInCDATA() throws Exception {
        // not possible, character is invalid in XML
    }

    // inherits tests from superclass
}
