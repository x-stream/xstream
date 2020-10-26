/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
