package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.CannotParseXMLException;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLReaderDriver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DomXMLReaderDriver implements XMLReaderDriver {

    public XMLReader createReader(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            Document document = documentBuilder.parse(inputStream);
            return new DomXMLReader(document);
        } catch (FactoryConfigurationError e) {
            throw new CannotParseXMLException(e);
        } catch (ParserConfigurationException e) {
            throw new CannotParseXMLException(e);
        } catch (SAXException e) {
            throw new CannotParseXMLException(e);
        } catch (IOException e) {
            throw new CannotParseXMLException(e);
        }
    }

}
