/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Uses XStream with a compact XML output format.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream
 * @see CompactWriter
 */
public class XStreamCompact implements Product {

    private final XStream xstream;

    public XStreamCompact() {
        this.xstream = new XStream(new XppDriver());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.marshal(object, new CompactWriter(new OutputStreamWriter(output)));
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream (Compact XML)";
    }

}
