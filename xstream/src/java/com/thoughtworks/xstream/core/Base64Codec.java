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

package com.thoughtworks.xstream.core;

import java.util.Base64;

import com.thoughtworks.xstream.core.StringCodec;


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
