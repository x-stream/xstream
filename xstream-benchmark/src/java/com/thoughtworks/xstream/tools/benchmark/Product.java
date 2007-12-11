/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Provides an abstraction above the product used to perform the serialization/deserialization
 * in the benchmarks.
 *
 * @author Joe Walnes
 * @see Harness
 */
public interface Product {

    /**
     * Serialize an object to a stream.
     */
    void serialize(Object object, OutputStream output) throws Exception;

    /**
     * Deserialize an object from a stream.
     */
    Object deserialize(InputStream input) throws Exception;
    
}
