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

import javax.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.core.Base64Codec;
import com.thoughtworks.xstream.core.StringCodec;


/**
 * Base64 codec implementation based on JAXB.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.11
 * @deprecated As of upcoming use {@link Base64Codec}
 */
@Deprecated
public class Base64JAXBCodec implements StringCodec {

    @Override
    public byte[] decode(final String base64) {
        return DatatypeConverter.parseBase64Binary(base64);
    }

    @Override
    public String encode(final byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
}
