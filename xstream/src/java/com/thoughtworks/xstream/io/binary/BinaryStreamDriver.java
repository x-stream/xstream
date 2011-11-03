/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. October 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.binary;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * HierarchicalStreamDriver for binary input and output. The driver uses an optimized binary
 * format to store an object graph. The format is not as compact as Java serialization, but a
 * lot more than typical text-based formats like XML. However, due to its nature it cannot use a
 * {@link Reader} for input or a {@link Writer} for output.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.2
 */
public class BinaryStreamDriver extends AbstractDriver {

    /**
     * @throws UnsupportedOperationException if called
     */
    public HierarchicalStreamReader createReader(Reader in) {
        throw new UnsupportedOperationException(
            "The BinaryDriver cannot use character-oriented input streams.");
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return new BinaryStreamReader(in);
    }

    /**
     * @throws UnsupportedOperationException if called
     */
    public HierarchicalStreamWriter createWriter(Writer out) {
        throw new UnsupportedOperationException(
            "The BinaryDriver cannot use character-oriented output streams.");
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return new BinaryStreamWriter(out);
    }
}
