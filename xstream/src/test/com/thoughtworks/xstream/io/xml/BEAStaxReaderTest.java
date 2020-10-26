/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
