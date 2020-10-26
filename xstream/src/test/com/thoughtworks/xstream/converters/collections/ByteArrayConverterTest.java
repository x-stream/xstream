/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 25. April 2004 by James Strachan
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
