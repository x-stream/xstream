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

package com.thoughtworks.xstream.converters.extended;

import java.util.regex.Pattern;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class RegexPatternConverterTest extends AbstractAcceptanceTest {

    public void testHandlesSimplePattern() {
        final Pattern root = Pattern.compile(".*");
        final String xml = ""//
            + "<java.util.regex.Pattern>\n"
            + "  <pattern>.*</pattern>\n"
            + "  <flags>0</flags>\n"
            + "</java.util.regex.Pattern>";
        assertBothWays(root, xml);
    }

    public void testHandlesPatternWithStartAndEnd() {
        final Pattern root = Pattern.compile("^[a-z0-9]{8}$");
        final String xml = "" //
            + "<java.util.regex.Pattern>\n"
            + "  <pattern>^[a-z0-9]{8}$</pattern>\n"
            + "  <flags>0</flags>\n"
            + "</java.util.regex.Pattern>";
        assertBothWays(root, xml);
    }

}
