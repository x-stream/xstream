/*
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. October 2006 by Joerg Schaible
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
