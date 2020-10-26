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

package com.thoughtworks.xstream.io.xml.xppdom;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * XmlPullParser utility methods.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.1
 */
public class XppFactory {
    
    /**
     * Create a new XmlPullParser using the XPP factory.
     * 
     * @return a new parser instance
     * @throws XmlPullParserException if the factory fails
     * @since 1.4.1
     */
    public static XmlPullParser createDefaultParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        return factory.newPullParser();
    }
    
    /**
     * Build an XPP DOM hierarchy from a String.
     * 
     * @param xml the XML data
     * @throws XmlPullParserException if the default parser cannot be created or fails with invalid XML
     * @throws IOException if the data cannot be read
     * @see XppDom#build(XmlPullParser)
     * @since 1.4.1
     */
    public static XppDom buildDom(String xml) throws XmlPullParserException, IOException {
        return buildDom(new StringReader(xml));
    }
    
    /**
     * Build an XPP DOM hierarchy from a Reader.
     * 
     * @param r the reader
     * @throws XmlPullParserException if the default parser cannot be created or fails with invalid XML
     * @throws IOException if the data cannot be read
     * @see XppDom#build(XmlPullParser)
     * @since 1.4.1
     */
    public static XppDom buildDom(Reader r) throws XmlPullParserException, IOException {
        XmlPullParser parser = createDefaultParser();
        parser.setInput(r);
        return XppDom.build(parser);
    }
    
    /**
     * Build an XPP DOM hierarchy from an InputStream.
     * 
     * @param in the input stream
     * @param encoding the encoding of the input stream
     * @throws XmlPullParserException if the default parser cannot be created or fails with invalid XML
     * @throws IOException if the data cannot be read
     * @see XppDom#build(XmlPullParser)
     * @since 1.4.1
     */
    public static XppDom buildDom(InputStream in, String encoding) throws XmlPullParserException, IOException {
        XmlPullParser parser = createDefaultParser();
        parser.setInput(in, encoding);
        return XppDom.build(parser);
    }
}
