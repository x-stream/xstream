/*
 * Copyright (C) 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. November 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import java.io.Serializable;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToStringConverter;


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
        final XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    @XStreamAlias("annotated")
    public static class AnnotatedAttribute {
        @XStreamAsAttribute
        private String myField;
    }

    public void testAnnotation() {
        final AnnotatedAttribute value = new AnnotatedAttribute();
        value.myField = "hello";
        final String expected = "<annotated myField=\"hello\"/>";
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedAliasedAttribute {
        @XStreamAsAttribute
        @XStreamAlias("field")
        private String myField;
    }

    public void testAnnotationInCombinationWithAlias() {
        final AnnotatedAliasedAttribute value = new AnnotatedAliasedAttribute();
        value.myField = "hello";
        final String expected = "<annotated field=\"hello\"/>";
        assertBothWays(value, expected);
    }

    @SuppressWarnings("unused")
    @XStreamAlias("annotated")
    public static class AnnotatedAttributeParameterized<T> implements Serializable {
        private static final long serialVersionUID = 201401L;
        @XStreamAsAttribute
        private String myField;
    }

    public void testAnnotationInParameterizedClass() {
        final AnnotatedAttributeParameterized<String> value = new AnnotatedAttributeParameterized<>();
        value.myField = "hello";
        final String expected = "<annotated myField=\"hello\"/>";
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedGenericAttributeParameterized<T> implements Serializable {
        private static final long serialVersionUID = 201401L;
        @XStreamAsAttribute
        private T myField;
    }

    public void testAnnotationAtGenericTypeInParameterizedClass() {
        final AnnotatedGenericAttributeParameterized<String> value =
                new AnnotatedGenericAttributeParameterized<>();
        value.myField = "hello";
        final String expected = "" + "<annotated>\n" + "  <myField class=\"string\">hello</myField>\n" + "</annotated>";
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedGenericAttributeBounded<T extends Serializable> implements Serializable {
        private static final long serialVersionUID = 201401L;
        @XStreamAsAttribute
        private T myField;
    }

    public void testAnnotationAtGenericTypeInBoundedClass() {
        final AnnotatedGenericAttributeBounded<String> value = new AnnotatedGenericAttributeBounded<>();
        value.myField = "hello";
        final String expected = "" + "<annotated>\n" + "  <myField class=\"string\">hello</myField>\n" + "</annotated>";
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedGenericAttributeAndConverterParameterized<T> implements Serializable {
        private static final long serialVersionUID = 201401L;
        @XStreamAsAttribute
        @XStreamConverter(value = ToStringConverter.class, useImplicitType = false, types = {String.class})
        private T myField;
    }

    public void testAnnotationAtGenericTypeWithLocalConverterInParameterizedClass() {
        final AnnotatedGenericAttributeAndConverterParameterized<String> value =
                new AnnotatedGenericAttributeAndConverterParameterized<>();
        value.myField = "hello";
        final String expected = "<annotated myField=\"hello\"/>";
        assertBothWays(value, expected);
    }
}
