/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. August 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

/**
 * Interface for an encoder and decoder of data to string values and back.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.11
 */
public interface StringCodec {

    /**
     * Decode the provided encoded string.
     *
     * @param encoded the encoded string
     * @return the decoded data
     * @since 1.4.11
     */
    byte[] decode(String encoded);

    /**
     * Encode the provided data.
     *
     * @param data the data to encode
     * @return the data encoded as string
     * @since 1.4.11
     */
    String encode(byte[] data);
}