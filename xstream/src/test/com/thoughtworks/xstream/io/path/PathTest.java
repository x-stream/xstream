package com.thoughtworks.xstream.io.path;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PathTest {

    public static Test suite() {
        TestSuite result = new TestSuite("PathTest");

        addTest(result,
                "/a/b/c",
                "/a/b/c",
                ".");

        addTest(result,
                "/a",
                "/a/b/c",
                "b/c");

        addTest(result,
                "/a/b/c",
                "/a",
                "../..");

        addTest(result,
                "/a/b/c",
                "/a/b/X",
                "../X");

        addTest(result,
                "/a/b/c",
                "/a/X/c",
                "../../X/c");

        addTest(result,
                "/a/b/c/d",
                "/a/X/c",
                "../../../X/c");

        addTest(result,
                "/a/b/c",
                "/a/X/c/d",
                "../../X/c/d");

        addTest(result,
                "/a/b/c[2]",
                "/a/b/c[3]",
                "../c[3]");

        return result;
    }

    private static void addTest(TestSuite suite, final String from, final String to, final String relative) {
        String testName = from + " - " + to;
        suite.addTest(new TestCase(testName) {
            protected void runTest() throws Throwable {
                assertEquals(new Path(relative), new Path(from).relativeTo(new Path(to)));
                assertEquals(new Path(to), new Path(from).apply(new Path(relative)));
            }
        });
    }


}
