/*
 * Copyright (C) 2017, 2018, 2020 XStream Committers.
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

import com.thoughtworks.xstream.core.Base64Codec;


/**
 * Base64 codec implementation based on java.util.Base64.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.11
 * @deprecated As of upcoming use {@link Base64Codec}
 */
@Deprecated
public class Base64JavaUtilCodec extends Base64Codec {

    /**
     * Constructs a Base64JavaUtilCodec.
     * <p>
     * The implementation will use a basic encoder and a MIME decoder by default.
     * </p>
     *
     * @since 1.4.11
     * @deprecated As of upcoming use {@link Base64Codec#Base64Codec()}
     */
    @Deprecated
    public Base64JavaUtilCodec() {
        super();
    }

    /**
     * Constructs a Base64JavaUtilCodec with provided encoder and decoder.
     *
     * @param encoder the encoder instance
     * @param decoder the decoder instance
     * @since 1.4.11
     * @deprecated As of upcoming use
     *             {@link Base64Codec#Base64Codec(java.util.Base64.Encoder, java.util.Base64.Decoder)}
     */
    @Deprecated
    public Base64JavaUtilCodec(final Base64.Encoder encoder, final Base64.Decoder decoder) {
        super(encoder, decoder);
    }
}
