/*
 * Copyright (C) 2017, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. March 2020 by Joerg Schaible, renamed from com.thoughtworks.xstream.core.Base64JavaUtilCodec
 */
package com.thoughtworks.xstream.core;

import java.util.Base64;


/**
 * Base64 codec implementation based on java.util.Base64.
 *
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class Base64Codec implements StringCodec {
    final private Base64.Decoder decoder;
    final private Base64.Encoder encoder;

    /**
     * Constructs a Base64Codec.
     * <p>
     * The implementation will use a basic encoder and a MIME decoder by default.
     * </p>
     *
     * @since upcoming
     */
    public Base64Codec() {
        this(Base64.getEncoder(), Base64.getMimeDecoder());
    }

    /**
     * Constructs a Base64Codec with provided encoder and decoder.
     *
     * @param encoder the encoder instance
     * @param decoder the decoder instance
     * @since upcoming
     */
    public Base64Codec(final Base64.Encoder encoder, final Base64.Decoder decoder) {
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
