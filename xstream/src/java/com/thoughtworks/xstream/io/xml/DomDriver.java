package com.thoughtworks.xstream.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

public class DomDriver extends AbstractXmlDriver {
    
    private final String encoding;
    private final DocumentBuilderFactory documentBuilderFactory;

    public DomDriver() {
        this("UTF-8");
    }

    public DomDriver(String encoding) {
        this(encoding, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public DomDriver(String encoding, XmlFriendlyReplacer replacer) {
        super(replacer);
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.encoding = encoding;
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
        try {
			return createWriter(new OutputStreamWriter(out, this.encoding));
		} catch (UnsupportedEncodingException e) {
			throw new StreamException(e);
		}
    }
}
