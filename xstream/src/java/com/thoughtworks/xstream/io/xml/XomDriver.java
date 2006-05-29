package com.thoughtworks.xstream.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

public class XomDriver extends AbstractXmlFriendlyDriver {

    private final Builder builder;

    public XomDriver() {
        this(new Builder());
    }

    public XomDriver(Builder builder) {
        this(builder, new XmlFriendlyReplacer());
    }

    public XomDriver(XmlFriendlyReplacer replacer) {
        this(new Builder(), replacer);        
    }
    
    public XomDriver(Builder builder, XmlFriendlyReplacer replacer) {
        super(replacer);    
        this.builder = builder;
    }

    protected Builder getBuilder() {
        return this.builder;
    }

    public HierarchicalStreamReader createReader(Reader text) {
        try {
            Document document = builder.build(text);
            return xmlFriendlyReader(new XomReader(document));
        } catch (ValidityException e) {
            throw new StreamException(e);
        } catch (ParsingException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            Document document = builder.build(in);
            return xmlFriendlyReader(new XomReader(document));
        } catch (ValidityException e) {
            throw new StreamException(e);
        } catch (ParsingException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer out) {
        return xmlFriendlyWriter(new PrettyPrintWriter(out));
    }

    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return xmlFriendlyWriter(new PrettyPrintWriter(new OutputStreamWriter(out)));
    }
}
