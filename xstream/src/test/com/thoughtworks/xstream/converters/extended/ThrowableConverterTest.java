package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class ThrowableConverterTest extends AbstractAcceptanceTest {

    public void testDeserializesThrowable() {
        Throwable expected = new Throwable();
        Throwable result = (Throwable) xstream.fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testDeserializesException() {
        Exception expected = new Exception();
        Throwable result = (Throwable) xstream.fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesMessage() {
        Throwable expected = new Throwable("A MESSAGE");
        Throwable result = (Throwable) xstream.fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesCause() {
        Throwable expected = new Throwable(new Throwable());
        Throwable result = (Throwable) xstream.fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesCauseAndMessage() {
        Throwable expected = new Throwable("MESSAGE", new Throwable("CAUSE MESSAGE"));
        Throwable result = (Throwable) xstream.fromXML(xstream.toXML(expected));
        assertThrowableEquals(expected, result);
    }

    public void testIncludesStackTrace() {
        try {
            throw new Exception();
        } catch (Exception exception) {
            Throwable result = (Throwable) xstream.fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
        }
    }

    public static class MyException extends Exception {
        private BigDecimal number;

        public MyException(String msg, BigDecimal number) {
            super(msg);
            this.number = number;
        }

        public boolean equals(Object o) {
            return super.equals(o) && o instanceof MyException && number.equals(((MyException)o).number);
        }

    }

    public void testSerializesExtraFields() {
        try {
            throw new MyException("A MESSAGE", new BigDecimal(123.4));
        } catch (MyException exception) {
            Throwable result = (Throwable) xstream.fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
        }
    }
    
    public void testSerializesWithNoSelfReferenceForUninitializedCauseInJdk14() {
        xstream.setMode(XStream.NO_REFERENCES);
        try {
            throw new RuntimeException("Without cause");
        } catch (RuntimeException exception) {
            Throwable result = (Throwable) xstream.fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
            assertNull(exception.getCause());
            assertNull(result.getCause());
        }
    }
    
    public void testSerializesWithInitializedCauseInJdk14() {
        xstream.setMode(XStream.NO_REFERENCES);
        try {
            throw new RuntimeException("Without cause", null);
        } catch (RuntimeException exception) {
            Throwable result = (Throwable) xstream.fromXML(xstream.toXML(exception));
            assertThrowableEquals(exception, result);
            assertNull(exception.getCause());
            assertNull(result.getCause());
        }
    }

    private static void assertThrowableEquals(final Throwable a,
                                              final Throwable b) {
        assertBoth(a, b, new MoreAssertions() {
            public void assertMoreSafely(final Object a,
                                         final Object b) {
                final Throwable ta = (Throwable) a, tb = (Throwable) b;
                assertEquals(ta.getClass(), tb.getClass());
                assertEquals(ta.getMessage(), tb.getMessage());
                assertThrowableEquals(ta.getCause(), tb.getCause());
                assertArrayEquals(ta.getStackTrace(), tb.getStackTrace());
            }
        });
    }

    private static void assertArrayEquals(final Object[] expected, final Object[] actual) {
        StringBuffer expectedJoined = new StringBuffer();
        StringBuffer actualJoined = new StringBuffer();
        for (int i = 0; i < expected.length; i++) {
            expectedJoined.append(expected[i]).append('\n');
        }
        for (int i = 0; i < actual.length; i++) {
            // JRockit adds ":???" for invalid line number
            actualJoined.append(actual[i].toString().replaceFirst(":\\?\\?\\?", "")).append('\n');
        }
        assertEquals(expectedJoined.toString(), actualJoined.toString());
    }

    private static void assertBoth(Object a, Object b, MoreAssertions moreAssertions) {
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
