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
        assertEquals(new Path("/a"), pathTracker.getPath());

        reader.moveDown();
        assertEquals(new Path("/a/b"), pathTracker.getPath());

        reader.moveDown();
        assertEquals(new Path("/a/b/c"), pathTracker.getPath());

        reader.moveUp();
        assertEquals(new Path("/a/b"), pathTracker.getPath());

        reader.moveUp();
        reader.moveDown();
        assertEquals(new Path("/a/b[2]"), pathTracker.getPath());

        reader.moveUp();
        reader.moveDown();
        assertEquals(new Path("/a/d"), pathTracker.getPath());

        reader.moveUp();
        assertEquals(new Path("/a"), pathTracker.getPath());
    }
}
