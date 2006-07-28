package com.thoughtworks.xstream.io.xml;

import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

public class Dom4JDriver extends AbstractXmlDriver {

    private DocumentFactory documentFactory;
    private OutputFormat outputFormat;

    public Dom4JDriver() {
        this(new DocumentFactory(), OutputFormat.createPrettyPrint());
        outputFormat.setTrimText(false);
    }

    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat) {
        this(documentFactory, outputFormat, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat, XmlFriendlyReplacer replacer) {
        super(replacer);
        this.documentFactory = documentFactory;
        this.outputFormat = outputFormat;
    }


    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public HierarchicalStreamReader createReader(Reader text) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(text);
            return new Dom4JReader(document, xmlFriendlyReplacer());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            return new Dom4JReader(document, xmlFriendlyReplacer());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer out) {
        final HierarchicalStreamWriter[] writer = new HierarchicalStreamWriter[1];
        final FilterWriter filter = new FilterWriter(out){
            public void close() {
                writer[0].close();
            }
        };
        writer[0] = new Dom4JWriter(new XMLWriter(filter,  outputFormat), xmlFriendlyReplacer());
        return writer[0];
    }

    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        try {
            final HierarchicalStreamWriter[] writer = new HierarchicalStreamWriter[1];
            final FilterOutputStream filter = new FilterOutputStream(out){
                public void close() {
                    writer[0].close();
                }
            };
            writer[0] = new Dom4JWriter(new XMLWriter(filter,  outputFormat), xmlFriendlyReplacer());
            return writer[0];
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }
}
