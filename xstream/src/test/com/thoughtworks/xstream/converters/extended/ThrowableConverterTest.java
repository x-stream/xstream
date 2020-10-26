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

package com.thoughtworks.xstream.converters.extended;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
        final Throwable expected = new IOException("A MESSAGE");
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

    public void testIncludesSuppressedExceptions() {
        final Throwable expected = new Throwable("MESSAGE");
        expected.addSuppressed(new Throwable("SUPPRESSED MESSAGE"));
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

    public static class OtherException extends Exception {
        private static final long serialVersionUID = 201905L;
        private transient BigDecimal number;

        public OtherException(final String msg, final BigDecimal number) {
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

        private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            number = new BigDecimal(in.readDouble());
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeDouble(number.doubleValue());
        }
    }

    public void testSupportsSerializationMethods() {
        try {
            throw new OtherException("A MESSAGE", new BigDecimal(123.4));
        } catch (final OtherException exception) {
            final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
        }
    }

    public void testSerializesWithNoSelfReferenceForUninitializedCauseInPureMode() {
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

    public void testSerializesWithInitializedCauseInPureMode() {
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

    public void testCanUnmarshalFormatOf14() {
        final String xml = ""
            + "<java.lang.Throwable>\n"
            + "  <detailMessage>A MESSAGE</detailMessage>\n"
            + "  <stackTrace>\n"
            + "    <trace>com.thoughtworks.xstream.converters.extended.ThrowableConverterTest.testDeserializesThrowable(ThrowableConverterTest.java:26)</trace>\n"
            + "    <trace>sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)</trace>\n"
            + "    <trace>sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)</trace>\n"
            + "    <trace>sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)</trace>\n"
            + "    <trace>java.lang.reflect.Method.invoke(Method.java:498)</trace>\n"
            + "    <trace>junit.framework.TestCase.runTest(TestCase.java:154)</trace>\n"
            + "    <trace>junit.framework.TestCase.runBare(TestCase.java:127)</trace>\n"
            + "    <trace>junit.framework.TestResult$1.protect(TestResult.java:106)</trace>\n"
            + "    <trace>junit.framework.TestResult.runProtected(TestResult.java:124)</trace>\n"
            + "    <trace>junit.framework.TestResult.run(TestResult.java:109)</trace>\n"
            + "    <trace>junit.framework.TestCase.run(TestCase.java:118)</trace>\n"
            + "    <trace>junit.framework.TestSuite.runTest(TestSuite.java:208)</trace>\n"
            + "    <trace>junit.framework.TestSuite.run(TestSuite.java:203)</trace>\n"
            + "  </stackTrace>\n"
            + "</java.lang.Throwable>\n";
        final Throwable result = xstream.<Throwable>fromXML(xml);
        assertEquals("A MESSAGE", result.getMessage());
        assertEquals(13, result.getStackTrace().length);
    }

    public void testCanAddSuppressedExceptionsLater() {
        final Exception expected = new Exception();
        final Throwable result = xstream.<Throwable>fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
        result.addSuppressed(new RuntimeException());
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
                assertArrayEquals(ta.getSuppressed(), tb.getSuppressed());
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
