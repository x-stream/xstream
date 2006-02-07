package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;

public class FinalFieldsTest extends AbstractAcceptanceTest {

    static class ThingWithFinalField extends StandardObject {
        final int number = 9;
    }

    public void testSerializeFinalFieldsIfSupported() {
        xstream = new XStream(new Sun14ReflectionProvider());
        xstream.alias("thing", ThingWithFinalField.class);

        assertBothWays(new ThingWithFinalField(),
                "<thing>\n" +
                "  <number>9</number>\n" +
                "</thing>");
    }

    public void testExceptionThrownUponSerializationIfNotSupport() {
        xstream = new XStream(new PureJavaReflectionProvider());
        xstream.alias("thing", ThingWithFinalField.class);

        try {
            xstream.toXML(new ThingWithFinalField());
            if (!JVM.is15()) {
                fail("Expected exception");
            }
        } catch (ObjectAccessException expectedException) {
            assertEquals("Invalid final field " + ThingWithFinalField.class.getName() + ".number",
                    expectedException.getMessage());
        }
    }
}
