/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
        final AnnotatedAttributeParameterized<String> value = new AnnotatedAttributeParameterized<String>();
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
                new AnnotatedGenericAttributeParameterized<String>();
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
        final AnnotatedGenericAttributeBounded<String> value = new AnnotatedGenericAttributeBounded<String>();
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
                new AnnotatedGenericAttributeAndConverterParameterized<String>();
        value.myField = "hello";
        final String expected = "<annotated myField=\"hello\"/>";
        assertBothWays(value, expected);
    }
}
