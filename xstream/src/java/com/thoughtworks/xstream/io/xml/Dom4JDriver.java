package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.WriterWrapper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class Dom4JDriver implements HierarchicalStreamDriver {

    private DocumentFactory documentFactory;
    private OutputFormat outputFormat;

    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat) {
        this.documentFactory = documentFactory;
        this.outputFormat = outputFormat;
    }

    public Dom4JDriver() {
        this(new DocumentFactory(), OutputFormat.createPrettyPrint());
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
            return new Dom4JReader(document);
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            return new Dom4JReader(document);
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer out) {
        final Document document = documentFactory.createDocument();
        HierarchicalStreamWriter writer = new Dom4JWriter(document);

        // Ensure that on writer.close(), the Document is written back to the text output.
        writer = new WriterWrapper(writer) {
            public void close() {
                super.close();
                try {
                    XMLWriter writer = new XMLWriter(out, outputFormat);
                    writer.write(document);
                    writer.flush();
                } catch (IOException e) {
                    throw new StreamException(e);
                }
            }
        };

        return writer;
    }

    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        final Document document = documentFactory.createDocument();
        HierarchicalStreamWriter writer = new Dom4JWriter(document);

        // Ensure that on writer.close(), the Document is written back to the text output.
        writer = new WriterWrapper(writer) {
            public void close() {
                super.close();
                try {
                    XMLWriter writer = new XMLWriter(out, outputFormat);
                    writer.write(document);
                    writer.flush();
                } catch (IOException e) {
                    throw new StreamException(e);
                }
            }
        };

        return writer;
    }

}
