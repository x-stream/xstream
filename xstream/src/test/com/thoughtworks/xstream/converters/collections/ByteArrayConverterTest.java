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

package com.thoughtworks.xstream.converters.collections;

import java.util.Arrays;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class ByteArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallByteArrays() {
        final Dummy input = new Dummy(new byte[0]);

        final String expected = "" //
            + "<dummy>\n"
            + "  <data></data>\n"
            + "</dummy>";
        assertBothWays(input, expected);
    }

    public static class Dummy {
        byte[] data;

        @SuppressWarnings("unused")
        private Dummy() {
        }

        public Dummy(final byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public boolean equals(final Object that) {
            if (that instanceof Dummy) {
                return equals((Dummy)that);
            }
            return false;
        }

        public boolean equals(final Dummy that) {
            if (data == that.data) {
                return true;
            }
            if (data != null && that.data != null) {
                if (data.length == that.data.length) {
                    for (int i = 0; i < data.length; i++) {
                        final byte b1 = data[i];
                        final byte b2 = that.data[i];
                        if (b1 != b2) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;

        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("dummy", Dummy.class);
    }
}
