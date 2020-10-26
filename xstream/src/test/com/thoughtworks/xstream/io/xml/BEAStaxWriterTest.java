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

import java.util.Arrays;


public final class BEAStaxWriterTest extends AbstractStaxWriterTest {
    @Override
    protected void assertXmlProducedIs(String expected) {
        expected = expected.replaceAll(" xmlns=\"\"", "");
        expected = expected.replaceAll("<(\\w+)([^>]*)/>", "<$1$2></$1>");
        expected = expected.replace("&#xd;", "&#13;");
        expected = expected.replace("&#xa;", "&#10;");
        expected = expected.replace("&#x9;", "&#9;");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    @Override
    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='utf-8'?>";
    }

    @Override
    protected StaxDriver getStaxDriver() {
        return new BEAStaxDriver();
    }

    @Override
    protected void marshalRepairing(final QNameMap qnameMap, final String expected) {
        // repairing mode fails for BEA's reference implementation in these cases
        if (!(Arrays
            .asList("testNamespacedXmlWithPrefixTwice", "testNamespacedXmlWithSameAlias")
            .contains(getName()))) {
            super.marshalRepairing(qnameMap, expected);
        }
    }
}
