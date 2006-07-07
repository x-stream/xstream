package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.ConversionException;
import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author Paul Hammant
 */
public class ToStringConverterTest extends TestCase {

    public void testClaimsCanConvertRightType() throws NoSuchMethodException {
        SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertTrue(converter.canConvert(Foo.class));
    }

    public void testClaimsCantConvertWrongType() throws NoSuchMethodException {
        SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertFalse(converter.canConvert(Map.class));
    }

    public void testClaimsCantConvertWrongType2() {
        try {
            new ToStringConverter(Map.class);
            fail("shoulda barfed");
        } catch (NoSuchMethodException e) {
            // expected.
        }
    }

    public void testCanConvertRightType() throws NoSuchMethodException {
        SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertTrue(converter.fromString("hello") instanceof Foo);
        assertEquals("hello", ((Foo) converter.fromString("hello")).foo);
    }

    public void testCanInnocentlyConvertWrongTypeToString() throws NoSuchMethodException {
        SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertEquals("whoa", converter.toString("whoa"));
    }

    public void testCantConvertWrongType() throws NoSuchMethodException {
        SingleValueConverter converter = new ToStringConverter(BadFoo1.class);
        try {
            converter.fromString("whoa");
            fail("shoulda barfed");
        } catch (ConversionException e) {
            assertTrue(e.getMessage().startsWith("Unable to target single String param constructor"));
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }


    public static class Foo {
        final String foo;

        public Foo(String foo) {
            this.foo = foo;
        }

        public String toString() {
            return foo;
        }
    }

    public static class BadFoo1 {
        public BadFoo1(String string) {
            throw new NullPointerException("abc");
        }
    }


}
