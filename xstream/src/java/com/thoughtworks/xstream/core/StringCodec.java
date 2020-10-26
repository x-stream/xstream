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
