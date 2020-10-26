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
