/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.math.BigDecimal;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;


/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class ThrowableConverterTest extends AbstractAcceptanceTest {

    public void testDeserializesThrowable() {
        final Throwable expected = new Throwable();
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testDeserializesException() {
        final Exception expected = new Exception();
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesMessage() {
        final Throwable expected = new Throwable("A MESSAGE");
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesCause() {
        final Throwable expected = new Throwable(new Throwable());
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesCauseAndMessage() {
        final Throwable expected = new Throwable("MESSAGE", new Throwable("CAUSE MESSAGE"));
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesStackTrace() {
        try {
            throw new Exception();
        } catch (final Exception exception) {
            final Throwable result = (Throwable)xstream.fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
        }
    }

    public static class MyException extends Exception {
        private static final long serialVersionUID = 200405L;
        private final BigDecimal number;

        public MyException(final String msg, final BigDecimal number) {
            super(msg);
            this.number = number;
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o) && o instanceof MyException && number.equals(((MyException)o).number);
        }

        @Override
        public int hashCode() {
            return number.hashCode() | super.hashCode();
        }

    }

    public void testSerializesExtraFields() {
        try {
            throw new MyException("A MESSAGE", new BigDecimal(123.4));
        } catch (final MyException exception) {
            final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
        }
    }

    public void testSerializesWithNoSelfReferenceForUninitializedCauseInJdk14() {
        xstream.setMode(XStream.NO_REFERENCES);
        try {
            throw new RuntimeException("Without cause");
        } catch (final RuntimeException exception) {
            final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
            assertNull(exception.getCause());
            assertNull(result.getCause());
        }
    }

    public void testSerializesWithInitializedCauseInJdk14() {
        xstream.setMode(XStream.NO_REFERENCES);
        try {
            throw new RuntimeException("Without cause", null);
        } catch (final RuntimeException exception) {
            final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
            assertNull(exception.getCause());
            assertNull(result.getCause());
        }
    }

    private static void assertThrowableEquals(final Throwable a, final Throwable b) {
        assertBoth(a, b, new MoreAssertions() {
            @Override
            public void assertMoreSafely(final Object a, final Object b) {
                final Throwable ta = (Throwable)a, tb = (Throwable)b;
                assertEquals(ta.getClass(), tb.getClass());
                assertEquals(ta.getMessage(), tb.getMessage());
                assertThrowableEquals(ta.getCause(), tb.getCause());
                assertArrayEquals(ta.getStackTrace(), tb.getStackTrace());
            }
        });
    }

    private static void assertArrayEquals(final Object[] expected, final Object[] actual) {
        final StringBuffer expectedJoined = new StringBuffer();
        final StringBuffer actualJoined = new StringBuffer();
        for (final Object element : expected) {
            expectedJoined.append(element).append('\n');
        }
        for (final Object element : actual) {
            // JRockit adds ":???" for invalid line number
            actualJoined.append(element.toString().replaceFirst(":\\?\\?\\?", "")).append('\n');
        }
        assertEquals(expectedJoined.toString(), actualJoined.toString());
    }

    private static void assertBoth(final Object a, final Object b, final MoreAssertions moreAssertions) {
        if (null == a) {
            if (null == b) {
                return;
            } else {
                fail("Expected null, but was <" + b + ">");
            }
        } else if (null == b) {
            fail("Expected <" + a + "> but was null");
        } else {
            moreAssertions.assertMoreSafely(a, b);
        }
    }

    private interface MoreAssertions {
        void assertMoreSafely(final Object a, final Object b);
    }

}
