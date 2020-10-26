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

package com.thoughtworks.xstream.io.json;

import java.io.Writer;


/**
 * A simple writer that outputs JSON in a pretty-printed indented stream. Arrays, Lists and Sets rely on you NOT using
 * XStream.addImplicitCollection(..)
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.3.1, use JsonWriter instead
 */
@Deprecated
public class JsonHierarchicalStreamWriter extends JsonWriter {

    /**
     * @deprecated As of 1.3.1, use JsonWriter instead
     */
    @Deprecated
    public JsonHierarchicalStreamWriter(final Writer writer, final char[] lineIndenter, final String newLine) {
        super(writer, lineIndenter, newLine);
    }

    /**
     * @deprecated As of 1.3.1, use JsonWriter instead
     */
    @Deprecated
    public JsonHierarchicalStreamWriter(final Writer writer, final char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    /**
     * @deprecated As of 1.3.1, use JsonWriter instead
     */
    @Deprecated
    public JsonHierarchicalStreamWriter(final Writer writer, final String lineIndenter, final String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    /**
     * @deprecated As of 1.3.1, use JsonWriter instead
     */
    @Deprecated
    public JsonHierarchicalStreamWriter(final Writer writer, final String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    /**
     * @deprecated As of 1.3.1, use JsonWriter instead
     */
    @Deprecated
    public JsonHierarchicalStreamWriter(final Writer writer) {
        this(writer, new char[]{' ', ' '});
    }
}
