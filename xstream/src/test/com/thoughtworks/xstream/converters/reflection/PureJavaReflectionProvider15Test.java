/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. February 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;


public class PureJavaReflectionProvider15Test extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }


    // ---------------------------------------------------------


    public static class WithFinalField {
        private final String s;
        private WithFinalField() {
            this.s = "";
        }
        String getFinal() {
            return s;
        }
    }

    public void testCanCreateWithFinalFIeld() {
        assertCanCreate(WithFinalField.class);
    }

    public void testWriteToFinalField() {
        Object result = reflectionProvider.newInstance(WithFinalField.class);
        reflectionProvider.writeField(result, "s", "foo", WithFinalField.class);
        WithFinalField withFinalField = (WithFinalField)result;
        assertEquals("foo", withFinalField.getFinal());
    }

}

