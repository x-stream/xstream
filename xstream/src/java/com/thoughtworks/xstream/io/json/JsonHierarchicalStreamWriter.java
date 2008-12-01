/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.json;

import java.io.Writer;


/**
 * A simple writer that outputs JSON in a pretty-printed indented stream. Arrays, Lists and Sets
 * rely on you NOT using XStream.addImplicitCollection(..)
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated since 1.3.1, use JsonWriter instead
 */
public class JsonHierarchicalStreamWriter extends JsonWriter {

    /**
     * @deprecated since 1.3.1, use JsonWriter instead
     */
    public JsonHierarchicalStreamWriter(Writer writer, char[] lineIndenter, String newLine) {
        super(writer, lineIndenter, newLine);
    }

    /**
     * @deprecated since 1.3.1, use JsonWriter instead
     */
    public JsonHierarchicalStreamWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    /**
     * @deprecated since 1.3.1, use JsonWriter instead
     */
    public JsonHierarchicalStreamWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    /**
     * @deprecated since 1.3.1, use JsonWriter instead
     */
    public JsonHierarchicalStreamWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    /**
     * @deprecated since 1.3.1, use JsonWriter instead
     */
    public JsonHierarchicalStreamWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }
}
