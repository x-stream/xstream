package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for annotations mapping implicit collections.
 *
 * @author Lucio Benfante
 */
public class ImplicitCollectionTest extends AbstractAcceptanceTest {
            
    public void testSimpleImplicitCollection() {
        String expected = "" +
                "<root>\n" +
                "  <string>one</string>\n" +
                "  <string>two</string>\n" +
                "</root>";
        Annotations.configureAliases(xstream, ImplicitRootOne.class);
        ImplicitRootOne implicitRoot = new ImplicitRootOne();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testImplicitCollectionWithItemFieldName() {
        String expected = "" +
                "<root>\n" +
                "  <value>one</value>\n" +
                "  <value>two</value>\n" +
                "</root>";
        Annotations.configureAliases(xstream, ImplicitRootTwo.class);
        ImplicitRootTwo implicitRoot = new ImplicitRootTwo();
        implicitRoot.getValues().add("one");
        implicitRoot.getValues().add("two");
        assertBothWays(implicitRoot, expected);
    }

    public void testInvalidImplicitFieldAnnotation() {
        try {
            Annotations.configureAliases(xstream, InvalidImplicitRoot.class);
            fail("Thrown " + XStream.InitializationException.class.getName() + " expected");
        } catch (final XStream.InitializationException e) {
            assertTrue(e.getMessage().indexOf(InvalidImplicitRoot.class.getName() + ":value") > 0);
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
        @XStreamImplicit(itemFieldName="value")
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
        @XStreamImplicit(itemFieldName="outch")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
}
