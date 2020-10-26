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

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

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

    @Override
    public void testNullCharacterInValue() throws Exception {
        // not possible, null value is invalid in XML
    }
    
    @Override
    public void testSupportsFieldsWithSpecialCharsInXml11() throws Exception {
        // no support for XML 1.1
    }
    
    @Override
    public void testISOControlCharactersInValue() throws Exception {
        // not possible, only supported in XML 1.1
    }

    public void testISOControlCharactersInCDATA() throws Exception {
        final String content = "hello\u0004-\u0096world";
        final HierarchicalStreamReader xmlReader = createReader("<string><![CDATA[" + content + "]]></string>");
        assertEquals(content, xmlReader.getValue());
        xmlReader.close();
    }

    // inherits tests from superclass
}
