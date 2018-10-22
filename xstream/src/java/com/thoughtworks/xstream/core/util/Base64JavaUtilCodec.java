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
package com.thoughtworks.xstream.core.util;

import java.util.Base64;

import com.thoughtworks.xstream.core.StringCodec;


/**
 * Base64 codec implementation based on java.util.Base64.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.11
 */
public class Base64JavaUtilCodec implements StringCodec {
    final private Base64.Decoder decoder;
    final private Base64.Encoder encoder;

    /**
     * Constructs a Base64JavaUtilCodec.
     * <p>
     * The implementation will use a basic encoder and a MIME decoder by default.
     * </p>
     *
     * @since 1.4.11
     */
    public Base64JavaUtilCodec() {
        this(Base64.getEncoder(), Base64.getMimeDecoder());
    }

    /**
     * Constructs a Base64JavaUtilCodec with provided encoder and decoder.
     *
     * @param encoder the encoder instance
     * @param decoder the decoder instance
     * @since 1.4.11
     */
    public Base64JavaUtilCodec(final Base64.Encoder encoder, final Base64.Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public byte[] decode(final String base64) {
        return decoder.decode(base64);
    }

    @Override
    public String encode(final byte[] data) {
        return encoder.encodeToString(data);
    }
}
