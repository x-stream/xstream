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
