package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;


public class InheritanceTest extends AbstractAcceptanceTest {
    public void testHandlesInheritanceHeirarchies() {
        OpenSourceSoftware openSourceSoftware = new OpenSourceSoftware("apache", "geronimo", "license");
        String xml =
                "<oss>\n" +
                "  <license>license</license>\n" +
                "  <name>geronimo</name>\n" +
                "  <vendor>apache</vendor>\n" +
                "</oss>";

        xstream.alias("oss", OpenSourceSoftware.class);
        assertBothWays(openSourceSoftware, xml);
    }

    public static class ParentClass {
        private String name;

        public ParentClass(String name) {
            this.name = name;
        }

        public String getParentName() {
            return name;
        }
    }

    public static class ChildClass extends ParentClass {
        private String name;

        public ChildClass(String parentName, String childName) {
            super(parentName);
            this.name = childName;
        }

        public String getChildName() {
            return name;
        }

        public String toString() {
            return getParentName() + "/" + getChildName();
        }

        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }
    }

    public void testInheritanceHidingPrivateFieldOfSameName() {
        xstream.alias("parent", ParentClass.class);
        xstream.alias("child", ChildClass.class);

        ChildClass child = new ChildClass("PARENT", "CHILD");
        // sanity checks
        assertEquals("PARENT", child.getParentName());
        assertEquals("CHILD", child.getChildName());

        String expected = "" +
                "<child>\n" +
                "  <name>CHILD</name>\n" +
                "  <name defined-in=\"parent\">PARENT</name>\n" +
                "</child>";

        assertBothWays(child, expected);
    }
}
