package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.util.BitSet;

public class BitSetConverterTest extends AbstractAcceptanceTest {

    public void testConvertsToSingleCommaDelimitedString() {
        BitSet bitSet = new BitSet();
        bitSet.set(0, true);
        bitSet.set(1, true);
        bitSet.set(2, false);
        bitSet.set(3, true);
        bitSet.set(4, false);
        bitSet.set(5, true);
        bitSet.set(6, true);
        bitSet.set(7, false);
        bitSet.set(8, true);
        bitSet.set(9, false);
        bitSet.set(10, true);
        bitSet.set(11, false);

        String expected = "<bit-set>0,1,3,5,6,8,10</bit-set>";

        assertBothWays(bitSet, expected);
    }
}
