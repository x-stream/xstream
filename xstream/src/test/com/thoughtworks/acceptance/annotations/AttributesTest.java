package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * Tests annotations defining fields to be rendered as attributes.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class AttributesTest extends AbstractAcceptanceTest {

    @XStreamAlias("annotated")
    public static class AnnotatedAttribute {
        @XStreamAsAttribute
        private String myField;
    }

    public void testAnnotation() {
        AnnotatedAttribute value = new AnnotatedAttribute();
        value.myField = "hello";
        String expected = "<annotated myField=\"hello\"/>";
        xstream.processAnnotations(AnnotatedAttribute.class);
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedAliasedAttribute {
        @XStreamAsAttribute
        @XStreamAlias("field")
        private String myField;
    }

    public void testAnnotationInCombinationWithAlias() {
        AnnotatedAliasedAttribute value = new AnnotatedAliasedAttribute();
        value.myField = "hello";
        String expected = "<annotated field=\"hello\"/>";
        xstream.processAnnotations(AnnotatedAliasedAttribute.class);
        assertBothWays(value, expected);
    }
}
