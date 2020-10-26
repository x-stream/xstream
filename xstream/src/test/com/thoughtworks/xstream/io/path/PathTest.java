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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PathTest extends TestCase {

    public static Test suite() {
        final TestSuite result = new TestSuite(PathTest.class.getName());

        addTest(result, //
            "/a/b/c", //
            "/a/b/c", //
            ".", //
            ".", //
            true);

        addTest(result, //
            "/a", //
            "/a/b/c", //
            "b/c", //
            "b[1]/c[1]", //
            true);

        addTest(result, //
            "/a/b/c", //
            "/a", //
            "../..", //
            "../..", //
            false);

        addTest(result, //
            "/a/b/c", //
            "/a/b/X", //
            "../X", //
            "../X[1]", //
            false);

        addTest(result, //
            "/a/b/c", //
            "/a/X/c", //
            "../../X/c", //
            "../../X[1]/c[1]", //
            false);

        addTest(result, //
            "/a/b/c/d", //
            "/a/X/c", //
            "../../../X/c", //
            "../../../X[1]/c[1]", //
            false);

        addTest(result, //
            "/a/b/c", //
            "/a/X/c/d", //
            "../../X/c/d", //
            "../../X[1]/c[1]/d[1]", //
            false);

        addTest(result, //
            "/a/b/c[2]", //
            "/a/b/c[3]", //
            "../c[3]", //
            "../c[3]", //
            false);

        addTest(result, //
            "/a", //
            "/a[1]", //
            ".", //
            ".", //
            true);

        return result;
    }

    private static void addTest(final TestSuite suite, final String from, final String to, final String relative,
            final String explicit, final boolean isAncestor) {
        final String testName = from + " - " + to;
        suite.addTest(new TestCase(testName) {
            @Override
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
