package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

//import org.custommonkey.xmlunit.DetailedDiff;
//import org.custommonkey.xmlunit.Diff;
//import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;


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
    
    public void testCanConvertAnotherInstance() { //throws SAXException, IOException, ParserConfigurationException {
        XStream x = new XStream();
        final String xml = xstream.toXML(x);
        final XStream serialized = (XStream)xstream.fromXML(xml);
        final String xmlSerialized = xstream.toXML(serialized);
        assertNotNull(xmlSerialized);
        //assertEquals(xml, xmlSerialized);
//        Diff diff = new Diff(xml, xmlSerialized);
//        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
//        assertTrue(new DetailedDiff(diff).toString(), diff.similar());
    }
}
