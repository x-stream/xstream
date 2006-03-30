package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;


/**
 * @author J&ouml;rg Schaible
 */
public class XStreamConverterTest extends AbstractAcceptanceTest {

    private Transformer transformer; 
    
    protected void setUp() throws Exception {
        super.setUp();
        
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final URL url = getClass().getResource("XStream.xsl");
        transformer = transformerFactory.newTransformer(new StreamSource(url.openStream()));
    }

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
    
    public void testCanConvertAnotherInstance() throws TransformerException { 
        XStream x = new XStream();
        final String xml = normalizedXStreamXML(xstream.toXML(x));
        final XStream serialized = (XStream)xstream.fromXML(xml);
        final String xmlSerialized = normalizedXStreamXML(xstream.toXML(serialized));
        assertEquals(xml, xmlSerialized);
    }
    
    private String normalizedXStreamXML(String xml) throws TransformerException {
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }
}
