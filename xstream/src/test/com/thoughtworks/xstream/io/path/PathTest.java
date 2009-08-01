/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.path;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PathTest extends TestCase {

    public static Test suite() {
        TestSuite result = new TestSuite(PathTest.class.getName());

        addTest(result,
                "/a/b/c",
                "/a/b/c",
                ".",
                ".",
                true);

        addTest(result,
                "/a",
                "/a/b/c",
                "b/c",
                "b[1]/c[1]",
                true);

        addTest(result,
                "/a/b/c",
                "/a",
                "../..",
                "../..",
                false);

        addTest(result,
                "/a/b/c",
                "/a/b/X",
                "../X",
                "../X[1]",
                false);

        addTest(result,
                "/a/b/c",
                "/a/X/c",
                "../../X/c",
                "../../X[1]/c[1]",
                false);

        addTest(result,
                "/a/b/c/d",
                "/a/X/c",
                "../../../X/c",
                "../../../X[1]/c[1]",
                false);

        addTest(result,
                "/a/b/c",
                "/a/X/c/d",
                "../../X/c/d",
                "../../X[1]/c[1]/d[1]",
                false);

        addTest(result,
                "/a/b/c[2]",
                "/a/b/c[3]",
                "../c[3]",
                "../c[3]",
                false);

        addTest(result,
                "/a",
                "/a[1]",
                ".",
                ".",
                true);

        return result;
    }

    private static void addTest(TestSuite suite, final String from, final String to, final String relative, final String explicit, final boolean isAncestor) {
        String testName = from + " - " + to;
        suite.addTest(new TestCase(testName) {
            protected void runTest() throws Throwable {
                assertEquals(new Path(relative), new Path(from).relativeTo(new Path(to)));
                assertEquals(new Path(to), new Path(from).apply(new Path(relative)));
                assertEquals(isAncestor, new Path(from).isAncestor(new Path(to)));
                assertEquals(new Path(relative), new Path(explicit));
                assertEquals(new Path(relative).explicit(), explicit);
                assertEquals(relative, new Path(explicit).toString());
            }
        });
    }


}
