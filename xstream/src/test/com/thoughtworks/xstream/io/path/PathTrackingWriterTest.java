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

package com.thoughtworks.xstream.io.path;

import java.io.StringWriter;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import junit.framework.TestCase;


public class PathTrackingWriterTest extends TestCase {
    private StringWriter out;
    private HierarchicalStreamWriter writer;
    private PathTracker pathTracker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pathTracker = new PathTracker();
        out = new StringWriter();
        try (final HierarchicalStreamWriter originalWriter = new CompactWriter(out)) {
            writer = new PathTrackingWriter(originalWriter, pathTracker);
        }
    }

    public void testDecoratesXmlWriterProxyingAllInvocations() {

        writer.startNode("foo");
        writer.addAttribute("att", "something");
        writer.setValue("getValue");
        writer.endNode();

        assertEquals("<foo att=\"something\">getValue</foo>", out.toString());
    }

    public void testInterceptsWhenWriterMovesLocationInDocumentAndUpdatesPathTracker() {

        assertEquals(new Path(""), pathTracker.getPath());

        writer.startNode("foo");
        assertEquals(new Path("/foo"), pathTracker.getPath());

        writer.startNode("do");
        assertEquals(new Path("/foo/do"), pathTracker.getPath());

        writer.endNode();
        assertEquals(new Path("/foo"), pathTracker.getPath());

        writer.endNode();
        assertEquals(new Path(""), pathTracker.getPath());
    }

    public void testEncodesPathInTracker() {
        assertEquals(new Path(""), pathTracker.getPath());

        writer.startNode("foo");
        assertEquals(new Path("/foo"), pathTracker.getPath());

        writer.startNode("b_1");
        assertEquals(new Path("/foo/b__1"), pathTracker.getPath());
        assertEquals("b__1", pathTracker.peekElement());

        writer.endNode();
        assertEquals(new Path("/foo"), pathTracker.getPath());

        writer.endNode();
        assertEquals(new Path(""), pathTracker.getPath());
    }
}
