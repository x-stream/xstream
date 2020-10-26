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

package com.thoughtworks.xstream;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Core interface for a marshalling strategy.
 * <p>
 * An implementation dictates how an object graph is marshalled and unmarshalled. It is the implementation's
 * responsibility to deal with references between the objects.
 * </p>
 */
public interface MarshallingStrategy {

    /**
     * Marshal an object graph.
     * 
     * @param writer the target for the marshalled data
     * @param obj the object to marshal
     * @param converterLookup the converter store
     * @param mapper the mapper chain
     * @param dataHolder the holder for additional data and state while marshalling
     */
    void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper,
            DataHolder dataHolder);

    /**
     * Unmarshal an object graph.
     * 
     * @param root a possible root object (should be {@code null} in normal cases)
     * @param reader the source for the unmarshalled object data
     * @param dataHolder the holder for additional data and state while marshalling
     * @param converterLookup the converter store
     * @param mapper the mapper chain
     * @return the unmarshalled object
     */
    Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup,
            Mapper mapper);
}
