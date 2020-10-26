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

package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverterTest extends AbstractAcceptanceTest {

    public void testConvertsToSingleString() throws MalformedURLException {

        assertBothWays(
                new URL("http://www.apple.com:2020/path/blah.html?abc#2"),
                "<url>http://www.apple.com:2020/path/blah.html?abc#2</url>");

        assertBothWays(
                new URL("file:/c:/winnt/blah.txt"),
                "<url>file:/c:/winnt/blah.txt</url>");
    }

}
