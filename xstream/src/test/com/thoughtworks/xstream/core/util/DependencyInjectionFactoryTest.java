/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import junit.framework.TestCase;


public class DependencyInjectionFactoryTest extends TestCase {
    public void testDependencyInjectionWithMatchingParameterSequence() {
        final Exception exception = (Exception)DependencyInjectionFactory.newInstance(
                ObjectAccessException.class, new Object[]{"The message", this, new RuntimeException("JUnit")});
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message : JUnit", exception.getMessage());
        assertEquals("JUnit", ((ObjectAccessException)exception).getCause().getMessage());
    }

    public void testDependencyInjectionWillUseDefaultConstructor() {
        final String string = (String)DependencyInjectionFactory.newInstance(String.class, new Object[]{this});
        assertEquals("", string);
    }

    public void testDependencyInjectionWillMatchNullValue() {
        final Exception exception = (Exception)DependencyInjectionFactory.newInstance(
                ObjectAccessException.class, new Object[]{
                        new TypedNull(String.class), this, new RuntimeException("JUnit")});
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("null : JUnit", exception.getMessage());
    }

    public void testDependencyInjectionWillMatchPrimitives() {
        final String string = (String)DependencyInjectionFactory.newInstance(String.class, new Object[]{
                "JUnit".getBytes(), new Integer(1), this, new Integer(4)});
        assertEquals("Unit", string);
    }

    public void testDependencyInjectionWillArbitraryOrder() {
        final Exception exception = (Exception)DependencyInjectionFactory.newInstance(
                ObjectAccessException.class, new Object[]{new RuntimeException("JUnit"), this, "The message"});
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message : JUnit", exception.getMessage());
    }

    public void testDependencyInjectionWillMatchMostSpecificDependency() {
        final Exception exception = (Exception)DependencyInjectionFactory.newInstance(
                ObjectAccessException.class, new Object[]{
                        new RuntimeException("JUnit"), new IllegalArgumentException("foo"), this, "The message"});
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message : foo", exception.getMessage());
    }

    static class Thing {
        final TestCase testCase;
        final int first;
        final int second;

        public Thing(int first, int second, TestCase testCase) {
            this.first = first;
            this.second = second;
            this.testCase = testCase;
        }

        TestCase getTestCase() {
            return testCase;
        }

        int getFirst() {
            return first;
        }

        int getSecond() {
            return second;
        }
    }

    public void testDependencyInjectionWillMatchArbitraryOrderForOneAvailableConstructorOnly() {
        final Thing thing = (Thing)DependencyInjectionFactory.newInstance(Thing.class, new Object[]{
                this, new Integer(1), new Integer(2)});
        assertSame(this, thing.getTestCase());
        assertEquals(1, thing.getFirst());
        assertEquals(2, thing.getSecond());
    }
}
