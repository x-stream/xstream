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

        assertEquals("", pathTracker.getCurrentPath());

        writer.startNode("foo");
        assertEquals("/foo", pathTracker.getCurrentPath());

        writer.startNode("do");
        assertEquals("/foo/do", pathTracker.getCurrentPath());

        writer.endNode();
        assertEquals("/foo", pathTracker.getCurrentPath());

        writer.endNode();
        assertEquals("", pathTracker.getCurrentPath());

    }

}
