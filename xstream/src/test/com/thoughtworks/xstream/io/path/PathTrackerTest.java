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

        assertEquals("", pathTracker.getCurrentPath());

        // <root>
        pathTracker.pushElement("root");
        assertEquals("/root", pathTracker.getCurrentPath());

        //   <childA>
        pathTracker.pushElement("childA");
        assertEquals("/root/childA", pathTracker.getCurrentPath());
        //   </childA>
        pathTracker.popElement();
        assertEquals("/root", pathTracker.getCurrentPath());

        //   <childB>
        pathTracker.pushElement("childB");
        assertEquals("/root/childB", pathTracker.getCurrentPath());

        //     <grandchild>
        pathTracker.pushElement("grandchild");
        assertEquals("/root/childB/grandchild", pathTracker.getCurrentPath());
        //     </grandchild>
        pathTracker.popElement();
        assertEquals("/root/childB", pathTracker.getCurrentPath());

        //   </childB>
        pathTracker.popElement();
        assertEquals("/root", pathTracker.getCurrentPath());

        // </root>
        pathTracker.popElement();
        assertEquals("", pathTracker.getCurrentPath());

    }

    public void testAddsIndexIfSiblingOfSameTypeAlreadyExists() {

        // <root>
        pathTracker.pushElement("root");

        //   <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child", pathTracker.getCurrentPath());
        //   </child>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child[2]", pathTracker.getCurrentPath());
        //   </child>
        pathTracker.popElement();

        //   <another>
        pathTracker.pushElement("another");
        assertEquals("/root/another", pathTracker.getCurrentPath());
        //   </another>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child[3]", pathTracker.getCurrentPath());
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
        assertEquals("/root/child/child", pathTracker.getCurrentPath());
        //     </child>
        pathTracker.popElement();

        //     <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child/child[2]", pathTracker.getCurrentPath());
        //     </child>
        pathTracker.popElement();

        //   </child>
        pathTracker.popElement();

        //   <child>
        pathTracker.pushElement("child");

        //     <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child[2]/child", pathTracker.getCurrentPath());
        //     </child>
        pathTracker.popElement();

        //     <child>
        pathTracker.pushElement("child");
        assertEquals("/root/child[2]/child[2]", pathTracker.getCurrentPath());

        // ...
    }

}
