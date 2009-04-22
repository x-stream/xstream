/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.reflection.products;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.model.A100Parents;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses XStream with the XPP driver for parsing XML with aliases for field names.
 *
 * @author J&ouml;rg Schaible
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream#alias(String, Class)
 */
public class XStreamClassAliases implements Product {

    private final XStream xstream;

    public XStreamClassAliases() {
        this.xstream = new XStream(new XppDriver());
        for(int i = 0; i < 100; ++i) {
            String no = "00" + i;
            no = no.substring(no.length() - 3);
            try {
                Class cls = Class.forName(A100Parents.class.getName() + "$Parent" + no);
                xstream.alias("p" + no, cls);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream (class aliases)";
    }

}
