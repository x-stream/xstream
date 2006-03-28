package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;


/**
 * @author J&ouml;rg Schaible
 */
public class XStreamConverterTest extends AbstractAcceptanceTest {

    final static class ImplicitXStreamContainer {
        private XStream myXStream;
    };

    public void testDetectsSelfMarshalling() {
        ImplicitXStreamContainer c = new ImplicitXStreamContainer();
        c.myXStream = xstream;
        try {
            xstream.toXML(c);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
        }
    }
}
