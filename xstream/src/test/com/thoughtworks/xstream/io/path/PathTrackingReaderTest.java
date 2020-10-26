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
