package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import junit.framework.TestCase;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;

/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 */
public class StackTraceElementConverterTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.registerConverter(new StackTraceElementConverter());
    }

    public void testSerializesStackTraceElement() {
        StackTraceElement expected = createStackTraceElement(false);
        assertEquals(expected, xstream.fromXML(xstream.toXML(expected)));
    }

    public void testIncludesDebugInformation() {
        StackTraceElement expected = createStackTraceElement(true);
        assertEquals(expected, xstream.fromXML(xstream.toXML(expected)));
    }

    private StackTraceElement createStackTraceElement(boolean hasDebugInformation) {
        StackTraceElement element = new Throwable().getStackTrace()[0];

        if (!hasDebugInformation) {
            try {
                StackTraceElementConverter.setFileName(element, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return element;
    }
}
