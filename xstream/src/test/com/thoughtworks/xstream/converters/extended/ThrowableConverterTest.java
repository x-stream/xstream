package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class ThrowableConverterTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.registerConverter(new StackTraceElementConverter());
        xstream.registerConverter(new ThrowableConverter());
    }

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

    private static void assertThrowableEquals(final Throwable a,
                                              final Throwable b) {
        assertBoth(a, b, new MoreAssertions() {
            public void assertMoreSafely(final Object a,
                                         final Object b) {
                final Throwable ta = (Throwable) a, tb = (Throwable) b;
                assertEquals(ta.getClass(), tb.getClass());
                assertEquals(ta.getMessage(), tb.getMessage());
                assertThrowableEquals(ta.getCause(), tb.getCause());
                assertEquals(ta.getStackTrace(), tb.getStackTrace());
            }
        });
    }

    private static void assertEquals(final Object[] a, final Object[] b) {
        assertBoth(a, b, new MoreAssertions() {
            public void assertMoreSafely(Object a, Object b) {
                Object[] aa = (Object[]) a, ab = (Object[]) b;
                assertEquals(aa.length, ab.length);
                for (int i = 0; i < aa.length; i++) {
                    assertEquals(aa[i], ab[i]);
                }
            }
        });
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
