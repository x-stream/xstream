/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2014, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.json;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver for JSON that writes optimized JSON format, but is not able to deserialize the result.
 * 
 * @author Paul Hammant
 * @since 1.2
 */
public class JsonHierarchicalStreamDriver extends AbstractDriver {

    /**
     * Construct a JsonHierarchicalStreamDriver.
     */
    public JsonHierarchicalStreamDriver() {
        super();
    }

    /**
     * Construct a JsonHierarchicalStreamDriver with name coding.
     * 
     * @param nameCoder the coder to encode and decode the JSON labels.
     * @since 1.4.2
     */
    public JsonHierarchicalStreamDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    @Override
    public HierarchicalStreamReader createReader(final File in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    /**
     * Create a HierarchicalStreamWriter that writes JSON.
     */
    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new JsonWriter(out);
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
         // JSON spec requires UTF-8
         return createWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

}
