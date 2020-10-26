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

package com.thoughtworks.xstream.converters.enums;

import java.util.EnumSet;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


public class EnumSetConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
    }

    public void testPutsEnumsInCompactCommaSeparatedString() {
        xstream.alias("simple", SimpleEnum.class);
        final EnumSet<SimpleEnum> set = EnumSet.of(SimpleEnum.GREEN, SimpleEnum.BLUE);

        final String expectedXml = "<enum-set enum-type=\"simple\">GREEN,BLUE</enum-set>";

        assertEquals(expectedXml, xstream.toXML(set));
        assertEquals(set, xstream.fromXML(expectedXml));
    }

    public void testSupportsJumboEnumSetsForMoreThan64Elements() {
        xstream.alias("big", BigEnum.class);
        final EnumSet<BigEnum> jumboSet = EnumSet.allOf(BigEnum.class);

        final String expectedXml = "<enum-set enum-type=\"big\">"
            + "A1,B1,C1,D1,E1,F1,G1,H1,I1,J1,K1,L1,M1,N1,O1,P1,Q1,R1,S1,T1,U1,V1,W1,X1,Y1,Z1,"
            + "A2,B2,C2,D2,E2,F2,G2,H2,I2,J2,K2,L2,M2,N2,O2,P2,Q2,R2,S2,T2,U2,V2,W2,X2,Y2,Z2,"
            + "A3,B3,C3,D3,E3,F3,G3,H3,I3,J3,K3,L3,M3,N3,O3,P3,Q3,R3,S3,T3,U3,V3,W3,X3,Y3,Z3"
            + "</enum-set>";

        assertEquals(expectedXml, xstream.toXML(jumboSet));
        assertEquals(jumboSet, xstream.fromXML(expectedXml));
    }

    public void testSupportsPolymorphicEnums() {
        xstream.alias("poly", PolymorphicEnum.class);
        final EnumSet<PolymorphicEnum> set = EnumSet.allOf(PolymorphicEnum.class);

        final String expectedXml = "<enum-set enum-type=\"poly\">A,B,C</enum-set>";

        assertEquals(expectedXml, xstream.toXML(set));
        assertEquals(set, xstream.fromXML(expectedXml));
    }

    public void testEmptyEnumSet() {
        xstream.alias("simple", SimpleEnum.class);
        final EnumSet<SimpleEnum> set = EnumSet.noneOf(SimpleEnum.class);

        final String expectedXml = "<enum-set enum-type=\"simple\"></enum-set>";

        assertEquals(expectedXml, xstream.toXML(set));
        assertEquals(set, xstream.fromXML(expectedXml));
    }

}
