/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
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
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

public class XppDomDriver extends AbstractXmlDriver {
    
    public XppDomDriver() {
        super(new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppDomDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }
    
    public HierarchicalStreamReader createReader(Reader xml) {
        try {
            return new XppDomReader(Xpp3DomBuilder.build(xml), xmlFriendlyReplacer());
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return createReader(new InputStreamReader(in));
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PrettyPrintWriter(out, xmlFriendlyReplacer());
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
