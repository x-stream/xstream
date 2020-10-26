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
