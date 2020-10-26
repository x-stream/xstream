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

import java.io.StringReader;

import org.xmlpull.mxp1.MXParser;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class XppReaderTest extends AbstractXMLReaderTest {
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new XppReader(new StringReader(xml), XppDriver.createDefaultParser());
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("resolve entity")) {
                throw e;
            }
        }
    }
    
    @Override
    public void testSupportsFieldsWithSpecialCharsInXml11() throws Exception {
        // no support for XML 1.1 if XPP implementation is Xpp3
        if (!(XppDriver.createDefaultParser() instanceof MXParser)) {
            super.testSupportsFieldsWithSpecialCharsInXml11();
        }
    }

    // inherits tests from superclass
}
