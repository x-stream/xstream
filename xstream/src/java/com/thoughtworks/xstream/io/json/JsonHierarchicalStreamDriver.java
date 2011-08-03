/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

/**
 * A driver for JSON that writes optimized JSON format, but is not able to deserialize the result.
 * 
 * @author Paul Hammant
 * @since 1.2
 */
public class JsonHierarchicalStreamDriver implements HierarchicalStreamDriver {
    
    public HierarchicalStreamReader createReader(Reader in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    public HierarchicalStreamReader createReader(URL in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    public HierarchicalStreamReader createReader(File in) {
        throw new UnsupportedOperationException("The JsonHierarchicalStreamDriver can only write JSON");
    }

    /**
     * Create a HierarchicalStreamWriter that writes JSON.
     */
    public HierarchicalStreamWriter createWriter(Writer out) {
        return new JsonWriter(out);
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        try {
            // JSON spec requires UTF-8
            return createWriter(new OutputStreamWriter(out, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new StreamException(e);
        }
    }

}
