/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
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
