package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
        return createReader(new InputSource(xml));
    }

    public HierarchicalStreamReader createReader(InputStream xml) {
        return createReader(new InputSource(xml));
    }

    private HierarchicalStreamReader createReader(InputSource source) {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
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

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
