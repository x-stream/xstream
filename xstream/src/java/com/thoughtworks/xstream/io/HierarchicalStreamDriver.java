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

package com.thoughtworks.xstream.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

/**
 * Provides implementation of stream parsers and writers to XStream.
 *
 * @author Joe Walnes
 * @author James Strachan
 */
public interface HierarchicalStreamDriver {

    /**
     * Create the HierarchicalStreamReader with the stream parser reading from the IO reader.
     * 
     * @param in the {@link Reader} with the data to parse 
     * @return the HierarchicalStreamReader
     */
    HierarchicalStreamReader createReader(Reader in);
    
    /** 
     * Create the HierarchicalStreamReader with the stream parser reading from the input stream.
     * 
     * @param in the {@link InputStream} with the data to parse 
     * @since 1.1.3 
     */
    HierarchicalStreamReader createReader(InputStream in);

    /**
     * Create the HierarchicalStreamReader with the stream parser reading from a URL.
     * 
     * Depending on the parser implementation, some might take the URL as SystemId to resolve
     * additional references.
     * 
     * @param in the {@link URL} defining the location with the data to parse 
     * @return the HierarchicalStreamReader
     * @since 1.4
     */
    HierarchicalStreamReader createReader(URL in);

    /**
     * Create the HierarchicalStreamReader with the stream parser reading from a File.
     * 
     * Depending on the parser implementation, some might take the file path as SystemId to
     * resolve additional references.
     * 
     * @param in the {@link URL} defining the location with the data to parse 
     * @return the HierarchicalStreamReader
     * @since 1.4
     */
    HierarchicalStreamReader createReader(File in);

    /**
     * Create the HierarchicalStreamWriter with the formatted writer.
     * 
     * @param out the {@link Writer} to receive the formatted data 
     * @return the HierarchicalStreamWriter
     */
    HierarchicalStreamWriter createWriter(Writer out);
    /** 
     * Create the HierarchicalStreamWriter with the formatted writer.
     * 
     * @param out the {@link OutputStream} to receive the formatted data 
     * @return the HierarchicalStreamWriter
     * @since 1.1.3
     */
    HierarchicalStreamWriter createWriter(OutputStream out);

}
