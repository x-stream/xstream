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

        writer.startElement("foo");
        writer.addAttribute("att", "something");
        writer.writeText("text");
        writer.endElement();

        assertEquals("<foo att=\"something\">text</foo>", out.toString());
    }

    public void testInterceptsWhenWriterMovesLocationInDocumentAndUpdatesPathTracker() {

        assertEquals("", pathTracker.getCurrentPath());

        writer.startElement("foo");
        assertEquals("/foo", pathTracker.getCurrentPath());

        writer.startElement("do");
        assertEquals("/foo/do", pathTracker.getCurrentPath());

        writer.endElement();
        assertEquals("/foo", pathTracker.getCurrentPath());

        writer.endElement();
        assertEquals("", pathTracker.getCurrentPath());

    }

}
