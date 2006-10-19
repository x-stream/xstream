package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.basic.BooleanConverter;

import java.util.List;
import java.util.ArrayList;

/**
 * @author David Blevins
 */
public class BooleanFieldsTest extends AbstractAcceptanceTest {

    public static class Musican {
        public String name;
        public String genre;
        public boolean alive;

        public Musican(String name, String genre, boolean alive) {
            this.name = name;
            this.genre = genre;
            this.alive = alive;
        }
    }

    public void testTrueFalseValues() {
        List jazzIcons = new ArrayList();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);

        String expectedXml =
                "<list>\n" +
                "  <musician>\n" +
                "    <name>Miles Davis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>false</alive>\n" +
                "  </musician>\n" +
                "  <musician>\n" +
                "    <name>Wynton Marsalis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>true</alive>\n" +
                "  </musician>\n" +
                "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }

    public void testYesNoValues() {
        List jazzIcons = new ArrayList();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);
        xstream.registerConverter(BooleanConverter.YES_NO);

        String expectedXml =
                "<list>\n" +
                "  <musician>\n" +
                "    <name>Miles Davis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>no</alive>\n" +
                "  </musician>\n" +
                "  <musician>\n" +
                "    <name>Wynton Marsalis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>yes</alive>\n" +
                "  </musician>\n" +
                "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }

    public void testBinaryValues() {
        List jazzIcons = new ArrayList();
        jazzIcons.add(new Musican("Miles Davis", "jazz", false));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", true));

        xstream.alias("musician", Musican.class);
        xstream.registerConverter(BooleanConverter.BINARY);

        String expectedXml =
                "<list>\n" +
                "  <musician>\n" +
                "    <name>Miles Davis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>0</alive>\n" +
                "  </musician>\n" +
                "  <musician>\n" +
                "    <name>Wynton Marsalis</name>\n" +
                "    <genre>jazz</genre>\n" +
                "    <alive>1</alive>\n" +
                "  </musician>\n" +
                "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }

}
