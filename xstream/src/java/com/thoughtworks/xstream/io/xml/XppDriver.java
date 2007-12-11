/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XppDriver extends AbstractXmlDriver {
    
    private static boolean xppLibraryPresent;

    public XppDriver() {
        super(new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    public HierarchicalStreamReader createReader(Reader xml) {
        loadLibrary();
        return new XppReader(xml, xmlFriendlyReplacer());
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return createReader(new InputStreamReader(in));
    }

    private void loadLibrary() {
        if (!xppLibraryPresent) {
            try {
                Class.forName("org.xmlpull.mxp1.MXParser");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("XPP3 pull parser library not present. Specify another driver." +
                        " For example: new XStream(new DomDriver())");
            }
            xppLibraryPresent = true;
        }
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PrettyPrintWriter(out, xmlFriendlyReplacer());
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
