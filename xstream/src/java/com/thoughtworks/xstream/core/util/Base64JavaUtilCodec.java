/*
 * Copyright (C) 2017 XStream Committers.
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
 * @since upcoming
 */
public class Base64JavaUtilCodec implements StringCodec {
    final private Base64.Decoder decoder = Base64.getMimeDecoder();
    final private Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public byte[] decode(final String base64) {
        return decoder.decode(base64);
    }

    @Override
    public String encode(final byte[] data) {
        return encoder.encodeToString(data);
    }
}
