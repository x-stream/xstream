/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. May 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.strings.products;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses String.intern() for StringConverter (and allocates PermSpace).
 *
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 */
public class StringInternConverter implements Product {

    private final XStream xstream;

    public StringInternConverter() {
        xstream = new XStream(new XppDriver());
        xstream.registerConverter(new StringConverter());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "StringConverter using intern() allocating perm space";
    }

    /**
     * Converts a String to a String ;). Well ok, it doesn't
     * <i>actually</i> do any conversion.
     * <p>The converter always calls intern() on the returned
     * String to encourage the JVM to reuse instances.</p>
     *
     * @author Joe Walnes
     * @see String#intern()
     */
    public static class StringConverter extends AbstractSingleValueConverter {

        public boolean canConvert(Class type) {
            return type.equals(String.class);
        }

        public Object fromString(String str) {
            return str.intern();
        }

    }

}
