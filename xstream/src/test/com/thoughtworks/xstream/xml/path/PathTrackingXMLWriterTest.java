package com.thoughtworks.xstream.xml.path;

import junit.framework.TestCase;
import com.thoughtworks.xstream.xml.XMLWriter;
import com.thoughtworks.xstream.xml.text.CompactXMLWriter;

import java.io.StringWriter;

public class PathTrackingXMLWriterTest extends TestCase {
    private StringWriter out;
    private XMLWriter writer;
    private PathTracker pathTracker;

    protected void setUp() throws Exception {
        super.setUp();
        pathTracker = new PathTracker();
        out = new StringWriter();
        XMLWriter originalWriter = new CompactXMLWriter(out);

        writer = new PathTrackingXMLWriter(originalWriter, pathTracker);
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
