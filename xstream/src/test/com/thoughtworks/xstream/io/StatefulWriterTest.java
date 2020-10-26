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

package com.thoughtworks.xstream.io;

import java.io.IOException;
import java.io.StringWriter;

import com.thoughtworks.xstream.io.xml.CompactWriter;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class StatefulWriterTest extends TestCase {

    private StatefulWriter writer;
    private StringWriter stringWriter;

    @SuppressWarnings("resource")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stringWriter = new StringWriter();
        writer = new StatefulWriter(new CompactWriter(stringWriter));
    }

    public void testDelegatesAllCalls() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.setValue("foo");
        writer.endNode();
        writer.close();
        assertEquals("<junit test=\"true\">foo</junit>", stringWriter.toString());
    }

    public void testKeepsBlance() {
        writer.startNode("junit");
        writer.endNode();
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }

    public void testCanOnlyWriteAttributesToOpenNode() {
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.startNode("junit");
        writer.setValue("text");
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.endNode();
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }

    public void testCanWriteAttributesOnlyOnce() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        try {
            writer.addAttribute("test", "true");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.endNode();
    }

    public void testCanWriteValueOnlyToOpenNode() {
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
        writer.startNode("junit");
        writer.endNode();
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }

    public void testCannotOpenNodeInValue() {
        writer.startNode("junit");
        writer.setValue("test");
        try {
            writer.startNode("junit");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IllegalStateException);
        }
    }

    public void testCanCloseInFinally() {
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            writer.close();
        }
    }

    public void testCannotWriteAfterClose() {
        writer.close();
        try {
            writer.startNode("junit");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.addAttribute("junit", "test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.setValue("test");
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.endNode();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
        try {
            writer.flush();
            fail("Thrown " + StreamException.class.getName() + " expected");
        } catch (final StreamException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }

    public void testCanCloseTwice() {
        writer.close();
        writer.close();
    }

    public void testCaresAboutNestingLevelWritingAttributes() {
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.startNode("junit");
        writer.addAttribute("test", "true");
        writer.endNode();
        writer.endNode();
    }
}
