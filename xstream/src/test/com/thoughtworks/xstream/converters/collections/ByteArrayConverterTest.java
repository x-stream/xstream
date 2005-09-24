package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class ByteArrayConverterTest extends AbstractAcceptanceTest {


    public void testMarshallByteArrays() {
        Dummy input = new Dummy(new byte[0]);

        String expected = "<dummy>\n  <data></data>\n</dummy>";
        assertBothWays(input, expected);
    }

    public static class Dummy {
        byte[] data;
        
        private Dummy() {
        }

        public Dummy(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        public boolean equals(Object that) {
            if (that instanceof Dummy) {
                return equals((Dummy) that);
            }
            return false;
        }

        public boolean equals(Dummy that) {
            if (this.data == that.data) {
                return true;
            }
            if (this.data != null && that.data != null) {
                if (this.data.length == that.data.length) {
                    for (int i = 0; i < data.length; i++) {
                        byte b1 = data[i];
                        byte b2 = that.data[i];
                        if (b1 != b2) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;

        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("dummy", Dummy.class);
    }
}
