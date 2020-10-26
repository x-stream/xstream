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

package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.basic.BooleanConverter;


/**
 * @author David Blevins
 */
public class BooleanFieldsTest extends AbstractAcceptanceTest {

    public static class Musican {
        public String name;
        public String genre;
        public boolean alive;

        public Musican(final String name, final String genre, final boolean alive) {
            this.name = name;
            this.genre = genre;
            this.alive = alive;
        }
    }

    public void testTrueFalseValues() {
        final List<Musican> jazzIcons = new ArrayList<>();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);

        final String expectedXml = ""//
            + "<list>\n"
            + "  <musician>\n"
            + "    <name>Miles Davis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>false</alive>\n"
            + "  </musician>\n"
            + "  <musician>\n"
            + "    <name>Wynton Marsalis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>true</alive>\n"
            + "  </musician>\n"
            + "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }

    public void testYesNoValues() {
        final List<Musican> jazzIcons = new ArrayList<>();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);
        xstream.registerConverter(BooleanConverter.YES_NO);

        final String expectedXml = ""//
            + "<list>\n"
            + "  <musician>\n"
            + "    <name>Miles Davis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>no</alive>\n"
            + "  </musician>\n"
            + "  <musician>\n"
            + "    <name>Wynton Marsalis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>yes</alive>\n"
            + "  </musician>\n"
            + "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }

    public void testBinaryValues() {
        final List<Musican> jazzIcons = new ArrayList<>();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);
        xstream.registerConverter(BooleanConverter.BINARY);

        final String expectedXml = ""//
            + "<list>\n"
            + "  <musician>\n"
            + "    <name>Miles Davis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>0</alive>\n"
            + "  </musician>\n"
            + "  <musician>\n"
            + "    <name>Wynton Marsalis</name>\n"
            + "    <genre>jazz</genre>\n"
            + "    <alive>1</alive>\n"
            + "  </musician>\n"
            + "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }
}
