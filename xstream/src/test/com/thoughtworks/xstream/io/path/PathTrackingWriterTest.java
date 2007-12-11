/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import junit.framework.TestCase;

import java.io.StringWriter;

public class PathTrackingWriterTest extends TestCase {
    private StringWriter out;
    private HierarchicalStreamWriter writer;
    private PathTracker pathTracker;

    protected void setUp() throws Exception {
        super.setUp();
        pathTracker = new PathTracker();
        out = new StringWriter();
        HierarchicalStreamWriter originalWriter = new CompactWriter(out);

        writer = new PathTrackingWriter(originalWriter, pathTracker);
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

}
