package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;


/**
 * Test for annotations mapping implicit collections.
 * 
 * @author Lucio Benfante
 * @author J&ouml;rg Schaible
 */
public class ImplicitCollectionTest extends AbstractAcceptanceTest {

    public void testForField() {
        String expected = ""
            + "<root>\n"
            + "  <string>one</string>\n"
            + "  <string>two</string>\n"
            + "</root>";
        xstream.processAnnotations(ImplicitRootOne.class);
        ImplicitRootOne implicitRoot = new ImplicitRootOne();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testForFieldWithItemFieldName() {
        String expected = ""
            + "<root>\n"
            + "  <value>one</value>\n"
            + "  <value>two</value>\n"
            + "</root>";
        xstream.processAnnotations(ImplicitRootTwo.class);
        ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testFailsForInvalidFieldType() {
        try {
            xstream.processAnnotations(InvalidImplicitRoot.class);
            // TODO: force exception, since XStream.processAnnotations() is currently a noop.
            xstream.getMapper().serializedClass(InvalidImplicitRoot.class);
            fail("Thrown " + InitializationException.class.getName() + " expected");
        } catch (final InitializationException e) {
            assertTrue(e.getMessage().indexOf("\"value\"") > 0);
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootOne {
        @XStreamImplicit()
        private List<String> values = new ArrayList<String>();

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class ImplicitRootTwo {
        @XStreamImplicit(itemFieldName = "value")
        private List<String> values = new ArrayList<String>();

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    @XStreamAlias("root")
    public static class InvalidImplicitRoot {
        @XStreamImplicit(itemFieldName = "outch")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @XStreamAlias("implicit")
    public static class ImplicitParameterizedType {
        @XStreamImplicit(itemFieldName = "line")
        private ArrayList<ArrayList<Point>> signatureLines;
    }

    @XStreamAlias("point")
    public static class Point {
        @XStreamAsAttribute
        private int x;
        @XStreamAsAttribute
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void testWorksForParameterizedTypes() {
        xstream.processAnnotations(new Class[]{ImplicitParameterizedType.class, Point.class});
        String xml = ""
            + "<implicit>\n"
            + "  <line>\n"
            + "    <point x=\"33\" y=\"11\"/>\n"
            + "  </line>\n"
            + "</implicit>";
        ImplicitParameterizedType root = new ImplicitParameterizedType();
        root.signatureLines = new ArrayList<ArrayList<Point>>();
        root.signatureLines.add(new ArrayList<Point>());
        root.signatureLines.get(0).add(new Point(33, 11));
        assertBothWays(root, xml);
    }

}
