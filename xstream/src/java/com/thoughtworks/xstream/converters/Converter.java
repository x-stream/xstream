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

package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converter implementations are responsible marshalling Java objects to/from textual data.
 * <p>
 * If an exception occurs during processing, a {@link ConversionException} should be thrown.
 * </p>
 * <p>
 * If working with the high level {@link com.thoughtworks.xstream.XStream} facade, you can register new converters using
 * the XStream.registerConverter() method.
 * </p>
 * <p>
 * If working with the lower level API, the {@link com.thoughtworks.xstream.converters.ConverterLookup} implementation
 * is responsible for looking up the appropriate converter.
 * </p>
 * <p>
 * Converters for object that can store all information in a single value should implement
 * {@link com.thoughtworks.xstream.converters.SingleValueConverter}.
 * {@link com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter} provides a starting point.
 * </p>
 * <p>
 * {@link com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter} provides a starting point for
 * objects that hold a collection of other objects (such as Lists and Maps).
 * </p>
 * 
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.XStream
 * @see com.thoughtworks.xstream.converters.ConverterLookup
 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter
 * @see com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter
 */
public interface Converter extends ConverterMatcher {

    /**
     * Convert an object to textual data.
     * 
     * @param source the object to be marshalled.
     * @param writer a stream to write to.
     * @param context a context that allows nested objects to be processed by XStream.
     */
    void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    /**
     * Convert textual data back into an object.
     * 
     * @param reader the stream to read the text from.
     * @param context a context that allows nested objects to be processed by XStream.
     * @return the resulting object.
     */
    Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

}
