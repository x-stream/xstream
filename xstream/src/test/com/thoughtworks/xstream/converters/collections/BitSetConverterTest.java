package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.util.BitSet;

public class BitSetConverterTest extends AbstractAcceptanceTest {

    public void testConvertsToSingleCommaDelimitedString() {
        BitSet bitSet = new BitSet();
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(5);
        bitSet.set(6);
        bitSet.set(8);
        bitSet.set(10);

        String expected = "<bit-set>0,1,3,5,6,8,10</bit-set>";

        assertBothWays(bitSet, expected);
    }
}
