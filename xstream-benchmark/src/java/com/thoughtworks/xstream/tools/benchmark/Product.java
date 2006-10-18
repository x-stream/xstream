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
