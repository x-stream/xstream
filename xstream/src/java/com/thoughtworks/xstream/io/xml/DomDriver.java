package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DomDriver implements HierarchicalStreamDriver {

    public HierarchicalStreamReader createReader(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            Document document = documentBuilder.parse(inputStream);
            return new DomReader(document);
        } catch (FactoryConfigurationError e) {
            throw new StreamException(e);
        } catch (ParserConfigurationException e) {
            throw new StreamException(e);
        } catch (SAXException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

}
