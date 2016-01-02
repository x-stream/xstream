/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;

public class XomDriver extends AbstractXmlDriver {

    private final Builder builder;

    public XomDriver() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * @deprecated As of 1.4.9, use {@link #XomDriver()} and overload {@link #createBuilder()} instead
     */
    public XomDriver(Builder builder) {
        this(builder, new XmlFriendlyNameCoder());
    }
    
    /**
     * @since 1.4
     */
    public XomDriver(NameCoder nameCoder) {
        super(nameCoder);
        this.builder = null;
    }
    
    /**
     * @since 1.4
     * @deprecated As of 1.4.9, use {@link #XomDriver(NameCoder)} and overload {@link #createBuilder()} instead
     */
    public XomDriver(Builder builder, NameCoder nameCoder) {
        super(nameCoder);
        this.builder = builder;
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link #XomDriver(NameCoder)} instead
     */
    public XomDriver(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }
    
    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link #XomDriver(NameCoder)} and overload {@link #createBuilder()} instead
     */
    public XomDriver(Builder builder, XmlFriendlyReplacer replacer) {
        this(builder, (NameCoder)replacer);
    }

    /**
     * @deprecated As of 1.4.9, overload {@link #createBuilder()} instead
     */
    protected Builder getBuilder() {
        return this.builder;
    }

    /**
     * Create the Builder instance.
     *
     * A XOM builder is a wrapper around a {@link org.xml.sax.XMLReader}
     * instance which is not thread-safe by definition. Therefore each reader
     * should use its own builder instance to avoid concurrency problems.
     *
     * Overload this method to configure the generated builder instances e.g.
     * to activate validation.
     *
     * @return the new builder
     * @since 1.4.9
     */
    protected Builder createBuilder() {
        final Builder builder = getBuilder();
        return builder != null ? builder : new Builder();
    }

    public HierarchicalStreamReader createReader(Reader text) {
        try {
            final Document document = createBuilder().build(text);
            return new XomReader(document, getNameCoder());
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
            final Document document = createBuilder().build(in);
            return new XomReader(document, getNameCoder());
        } catch (ValidityException e) {
            throw new StreamException(e);
        } catch (ParsingException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(URL in) {
        try {
            final Document document = createBuilder().build(in.toExternalForm());
            return new XomReader(document, getNameCoder());
        } catch (ValidityException e) {
            throw new StreamException(e);
        } catch (ParsingException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(File in) {
        try {
            final Document document = createBuilder().build(in);
            return new XomReader(document, getNameCoder());
        } catch (ValidityException e) {
            throw new StreamException(e);
        } catch (ParsingException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return new PrettyPrintWriter(new OutputStreamWriter(out), getNameCoder());
    }
}
