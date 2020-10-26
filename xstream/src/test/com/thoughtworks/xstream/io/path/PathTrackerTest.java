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

import junit.framework.TestCase;


public class PathTrackerTest extends TestCase {

    private PathTracker pathTracker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // small initial capacity to ensure resizing works
        pathTracker = new PathTracker(1);
    }

    public void testExposesXpathLikeExpressionOfLocationInWriter() {

        assertEquals(new Path(""), pathTracker.getPath());
        assertEquals(0, pathTracker.depth());

        // <root>
        pathTracker.pushElement("root");
        assertEquals(new Path("/root"), pathTracker.getPath());
        assertEquals(1, pathTracker.depth());
        assertEquals("root", pathTracker.peekElement());

        // <childA>
        pathTracker.pushElement("childA");
        assertEquals(new Path("/root/childA"), pathTracker.getPath());
        assertEquals(2, pathTracker.depth());
        assertEquals("childA", pathTracker.peekElement());
        // </childA>
        pathTracker.popElement();
        assertEquals(new Path("/root"), pathTracker.getPath());
        assertEquals(1, pathTracker.depth());
        assertEquals("root", pathTracker.peekElement());

        // <childB>
        pathTracker.pushElement("childB");
        assertEquals(new Path("/root/childB"), pathTracker.getPath());
        assertEquals(2, pathTracker.depth());
        assertEquals("childB", pathTracker.peekElement());

        // <grandchild>
        pathTracker.pushElement("grandchild");
        assertEquals(new Path("/root/childB/grandchild"), pathTracker.getPath());
        assertEquals(3, pathTracker.depth());
        assertEquals("grandchild", pathTracker.peekElement(0));
        assertEquals("childB", pathTracker.peekElement(-1));
        assertEquals("root", pathTracker.peekElement(-2));
        // </grandchild>
        pathTracker.popElement();
        assertEquals(new Path("/root/childB"), pathTracker.getPath());
        assertEquals(2, pathTracker.depth());
        assertEquals("childB", pathTracker.peekElement());

        // </childB>
        pathTracker.popElement();
        assertEquals(new Path("/root"), pathTracker.getPath());
        assertEquals(1, pathTracker.depth());
        assertEquals("root", pathTracker.peekElement());

        // </root>
        pathTracker.popElement();
        assertEquals(new Path(""), pathTracker.getPath());
        assertEquals(0, pathTracker.depth());

    }

    public void testAddsIndexIfSiblingOfSameTypeAlreadyExists() {

        // <root>
        pathTracker.pushElement("root");

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child"), pathTracker.getPath());
        // </child>
        pathTracker.popElement();

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]"), pathTracker.getPath());
        assertEquals("child[2]", pathTracker.peekElement());
        // </child>
        pathTracker.popElement();

        // <another>
        pathTracker.pushElement("another");
        assertEquals(new Path("/root/another"), pathTracker.getPath());
        // </another>
        pathTracker.popElement();

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[3]"), pathTracker.getPath());
        assertEquals("child[3]", pathTracker.peekElement());
        // </child>
        pathTracker.popElement();

        // ...
    }

    public void testAssociatesIndexOnlyWithDirectParent() {

        // <root>
        pathTracker.pushElement("root");

        // <child>
        pathTracker.pushElement("child");

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child/child"), pathTracker.getPath());
        // </child>
        pathTracker.popElement();

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child/child[2]"), pathTracker.getPath());
        // </child>
        pathTracker.popElement();

        // </child>
        pathTracker.popElement();

        // <child>
        pathTracker.pushElement("child");

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]/child"), pathTracker.getPath());
        // </child>
        pathTracker.popElement();

        // <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]/child[2]"), pathTracker.getPath());

        // ...
    }

}
