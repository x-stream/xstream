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

package com.thoughtworks.xstream.core.util;

import java.io.StringWriter;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class QuickWriterTest extends TestCase {

    public void testUnbuffered() {
        final StringWriter stringWriter = new StringWriter();
        try (QuickWriter writer = new QuickWriter(stringWriter, 0)) {
            writer.write("Joe");
            assertEquals(stringWriter.toString(), "Joe");
            writer.write(' ');
            assertEquals(stringWriter.toString(), "Joe ");
            writer.write("Walnes".toCharArray());
            assertEquals(stringWriter.toString(), "Joe Walnes");
        }
    }
}
