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

package com.thoughtworks.xstream.io.binary;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * HierarchicalStreamDriver for binary input and output. The driver uses an optimized binary format to store an object
 * graph. The format is not as compact as Java serialization, but a lot more than typical text-based formats like XML.
 * However, due to its nature it cannot use a {@link Reader} for input or a {@link Writer} for output.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.2
 */
public class BinaryStreamDriver extends AbstractDriver {

    /**
     * @throws UnsupportedOperationException if called
     */
    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        throw new UnsupportedOperationException("The BinaryDriver cannot use character-oriented input streams.");
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        return new BinaryStreamReader(in);
    }

    /**
     * @throws UnsupportedOperationException if called
     */
    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        throw new UnsupportedOperationException("The BinaryDriver cannot use character-oriented output streams.");
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return new BinaryStreamWriter(out);
    }
}
