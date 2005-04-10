package com.thoughtworks.xstream.io.path;

import junit.framework.TestCase;

public class PathTrackerTest extends TestCase {

    private PathTracker pathTracker;

    protected void setUp() throws Exception {
        super.setUp();
        // small initial capacity to ensure resizing works
        pathTracker = new PathTracker(1);
    }

    public void testExposesXpathLikeExpressionOfLocationInWriter() {

        assertEquals(new Path(""), pathTracker.getPath());

        // <root>
        pathTracker.pushElement("root");
        assertEquals(new Path("/root"), pathTracker.getPath());

        //   <childA>
        pathTracker.pushElement("childA");
        assertEquals(new Path("/root/childA"), pathTracker.getPath());
        //   </childA>
        pathTracker.popElement();
        assertEquals(new Path("/root"), pathTracker.getPath());

        //   <childB>
        pathTracker.pushElement("childB");
        assertEquals(new Path("/root/childB"), pathTracker.getPath());

        //     <grandchild>
        pathTracker.pushElement("grandchild");
        assertEquals(new Path("/root/childB/grandchild"), pathTracker.getPath());
        //     </grandchild>
        pathTracker.popElement();
        assertEquals(new Path("/root/childB"), pathTracker.getPath());

        //   </childB>
        pathTracker.popElement();
        assertEquals(new Path("/root"), pathTracker.getPath());

        // </root>
        pathTracker.popElement();
        assertEquals(new Path(""), pathTracker.getPath());

    }

    public void testAddsIndexIfSiblingOfSameTypeAlreadyExists() {

        // <root>
        pathTracker.pushElement("root");

        //   <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child"), pathTracker.getPath());
        //   </child>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]"), pathTracker.getPath());
        //   </child>
        pathTracker.popElement();

        //   <another>
        pathTracker.pushElement("another");
        assertEquals(new Path("/root/another"), pathTracker.getPath());
        //   </another>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[3]"), pathTracker.getPath());
        //   </child>
        pathTracker.popElement();

        // ...
    }

    public void testAssociatesIndexOnlyWithDirectParent() {

        // <root>
        pathTracker.pushElement("root");

        //   <child>
        pathTracker.pushElement("child");

        //     <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child/child"), pathTracker.getPath());
        //     </child>
        pathTracker.popElement();

        //     <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child/child[2]"), pathTracker.getPath());
        //     </child>
        pathTracker.popElement();

        //   </child>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");

        //     <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]/child"), pathTracker.getPath());
        //     </child>
        pathTracker.popElement();

        //     <child>
        pathTracker.pushElement("child");
        assertEquals(new Path("/root/child[2]/child[2]"), pathTracker.getPath());

        // ...
    }

}
