package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.XppReader;
import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

public class PathTrackingReaderTest extends TestCase {

    public void testDecoratesReaderAndTracksPath() {
        Reader input = new StringReader("" +
                "<a>" +
                "  <b><c/></b>" +
                "  <b/>" +
                "  <d/>" +
                "</a>");
        HierarchicalStreamReader reader = new XppReader(input);
        PathTracker pathTracker = new PathTracker();

        reader = new PathTrackingReader(reader, pathTracker);
        assertEquals("/a", pathTracker.getCurrentPath());

        reader.moveDown();
        assertEquals("/a/b", pathTracker.getCurrentPath());

        reader.moveDown();
        assertEquals("/a/b/c", pathTracker.getCurrentPath());

        reader.moveUp();
        assertEquals("/a/b", pathTracker.getCurrentPath());

        reader.moveUp();
        reader.moveDown();
        assertEquals("/a/b[2]", pathTracker.getCurrentPath());

        reader.moveUp();
        reader.moveDown();
        assertEquals("/a/d", pathTracker.getCurrentPath());

        reader.moveUp();
        assertEquals("/a", pathTracker.getCurrentPath());
    }
}
