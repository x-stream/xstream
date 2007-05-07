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
}
