/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. November 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
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
    
    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    @XStreamAlias("annotated")
    public static class AnnotatedAttribute {
        @XStreamAsAttribute
        private String myField;
    }

    public void testAnnotation() {
        AnnotatedAttribute value = new AnnotatedAttribute();
        value.myField = "hello";
        String expected = "<annotated myField=\"hello\"/>";
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
        assertBothWays(value, expected);
    }
}
