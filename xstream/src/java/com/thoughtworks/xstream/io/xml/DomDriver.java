package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.dom4j.io.XMLWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class DomDriver implements HierarchicalStreamDriver {

    private final String encoding;
    private final DocumentBuilderFactory documentBuilderFactory;

    public DomDriver(String encoding) {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.encoding = encoding;
    }

    public DomDriver() {
        this("UTF-8");
    }

    public HierarchicalStreamReader createReader(Reader xml) {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource source = new InputSource(xml);
            source.setEncoding(encoding);
            Document document = documentBuilder.parse(source);
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

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PrettyPrintWriter(out);
    }

}
