/*
 * Copyright (C) 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. March 2019 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * The replacement for the {@link XppDriver} as default for XStream.
 * <p>
 * The reader is based on the default StAX implementation of the Java runtime, but without namespace support. The writer
 * is implemented using a {@link PrettyPrintWriter}.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class SimpleStaxDriver extends AbstractDriver {

    private static final class StandardStaxDriverExtension extends StandardStaxDriver {
        /**
         * Constructs a StandardStaxDriverExtension.
         *
         * @param nameCoder
         * @since upcoming
         */
        private StandardStaxDriverExtension(final NameCoder nameCoder) {
            super(EMPTY_QNAME_MAP, nameCoder);
        }

        @Override
        protected XMLInputFactory createInputFactory() {
            final XMLInputFactory instance = super.createInputFactory();
            instance.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            return instance;
        }
    }

    private static final QNameMap EMPTY_QNAME_MAP = new QNameMap();
    private final StaxDriver driver;

    /**
     * Constructs a SimpleStaxDriver.
     *
     * @since upcoming
     */
    public SimpleStaxDriver() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * Constructs a SimpleStaxDriver.
     *
     * @param nameCoder
     * @since upcoming
     */
    public SimpleStaxDriver(final NameCoder nameCoder) {
        super(nameCoder);
        driver = new StandardStaxDriverExtension(nameCoder);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        return driver.createReader(in);
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        return driver.createReader(in);
    }

    @Override
    public HierarchicalStreamReader createReader(final File in) {
        return driver.createReader(in);
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        return driver.createReader(in);
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
