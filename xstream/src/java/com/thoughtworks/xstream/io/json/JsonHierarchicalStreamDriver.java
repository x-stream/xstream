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
