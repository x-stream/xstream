package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.InheritanceTest.ChildA;
import com.thoughtworks.acceptance.InheritanceTest.ParentA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test XStream 1.2 compatibility.
 * 
 * @author J&ouml;rg Schaible
 */
public class XStream12CompatibilityTest extends AbstractAcceptanceTest {

    public static class ParentClass {
        String name;
    }

    public static class ChildClass extends ParentClass {
        String name;

        ChildClass(final String parent, final String child) {
            ((ParentClass)this).name = parent;
            name = child;
        }
        
        public String toString() {
            return ((ParentClass)this).name + "/" + name;
        }
    }

    public void testCanDeserializeHiddenFieldsWithSameTypeWrittenWithXStream11() {
        xstream.alias("parent", ParentClass.class);
        xstream.alias("child", ChildClass.class);

        final String in = "" +
                "<child>\n" +
                "  <name>CHILD</name>\n" +
                "  <name defined-in=\"parent\">PARENT</name>\n" +
                "</child>";
        
        final ChildClass child = (ChildClass)xstream.fromXML(in);
        assertEquals("PARENT/CHILD", child.toString());
    }

    public static class ParentA extends StandardObject {
        private List stuff = new ArrayList();

        public List getParentStuff() {
            return stuff;
        }
    }

    public static class ChildA extends ParentA {
        private Map stuff = new HashMap();

        public Map getChildStuff() {
            return stuff;
        }
    }

    public void testCanDeserializeHiddenFieldsWithDifferentTypeWrittenWithXStream11() {
        xstream.alias("child-a", ChildA.class);
        xstream.alias("parent-a", ParentA.class);
        String expected = "" +
                "<child-a>\n" +
                "  <stuff>\n" +
                "    <entry>\n" +
                "      <string>hello</string>\n" +
                "      <string>world</string>\n" +
                "    </entry>\n" +
                "  </stuff>\n" +
                "  <stuff defined-in=\"parent-a\">\n" +
                "    <string>foo</string>\n" +
                "  </stuff>\n" +
                "</child-a>";
        
        ChildA childA = (ChildA)xstream.fromXML(expected);
        assertEquals("world", childA.getChildStuff().get("hello"));
        assertEquals("foo", childA.getParentStuff().iterator().next());
    }
}
