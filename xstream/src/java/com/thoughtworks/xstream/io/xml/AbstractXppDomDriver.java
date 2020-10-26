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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.core.util.XmlHeaderAwareReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;


/**
 * An abstract base class for a driver using an XPP DOM implementation.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractXppDomDriver extends AbstractXmlDriver {

    /**
     * Construct an AbstractXppDomDriver.
     * 
     * @param nameCoder the replacer for XML friendly names
     * @since 1.4
     */
    public AbstractXppDomDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        try {
            final XmlPullParser parser = createParser();
            parser.setInput(in);
            return new XppDomReader(XppDom.build(parser), getNameCoder());
        } catch (final XmlPullParserException | IOException e) {
            throw new StreamException(e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        try {
            return createReader(new XmlHeaderAwareReader(in));
        } catch (final UnsupportedEncodingException e) {
            throw new StreamException(e);
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }

    /**
     * Create the parser of the XPP implementation.
     * 
     * @throws XmlPullParserException if the parser cannot be created
     * @since 1.4
     */
    protected abstract XmlPullParser createParser() throws XmlPullParserException;
}
