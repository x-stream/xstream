/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.path;

import java.io.Reader;
import java.io.StringReader;

import org.xmlpull.mxp1.MXParser;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.XppReader;

import junit.framework.TestCase;


public class PathTrackingReaderTest extends TestCase {

    public void testDecoratesReaderAndTracksPath() {
        final Reader input = new StringReader("" + "<a>" + "  <b><c/></b>" + "  <b/>" + "  <d/>" + "</a>");
        @SuppressWarnings("resource")
        HierarchicalStreamReader reader = new XppReader(input, new MXParser());
        final PathTracker pathTracker = new PathTracker();

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

    public void testPathsAreDecodedInTracker() {
        final Reader input = new StringReader("" + "<a>" + "  <b__1/>" + "</a>");
        @SuppressWarnings("resource")
        HierarchicalStreamReader reader = new XppReader(input, new MXParser());
        final PathTracker pathTracker = new PathTracker();

        reader = new PathTrackingReader(reader, pathTracker);
        assertEquals(new Path("/a"), pathTracker.getPath());

        reader.moveDown();
        assertEquals(new Path("/a/b_1"), pathTracker.getPath());
        assertEquals("b_1", pathTracker.peekElement());

        reader.moveUp();
        assertEquals(new Path("/a"), pathTracker.getPath());
    }
}
