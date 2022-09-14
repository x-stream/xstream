package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanFieldsTest extends AbstractAcceptanceTest {

    public static class Musican {
        public String name;
        public String genre;
        public AtomicBoolean alive;

        public Musican(final String name, final String genre, final AtomicBoolean alive) {
            this.name = name;
            this.genre = genre;
            this.alive = alive;
        }
    }

    public void testAtomicBooleanFields() {
        final List<Musican> jazzIcons = new ArrayList<>();
        jazzIcons.add(new Musican("Miles Davis", "jazz", new AtomicBoolean(false)));
        jazzIcons.add(new Musican("Wynton Marsalis", "jazz", new AtomicBoolean(true)));

        xstream.alias("musician", Musican.class);

        final String expectedXml =
                "<list>\n"
                        + "  <musician>\n"
                        + "    <name>Miles Davis</name>\n"
                        + "    <genre>jazz</genre>\n"
                        + "    <alive>\n"
                        + "      <value>0</value>\n"
                        + "    </alive>\n"
                        + "  </musician>\n"
                        + "  <musician>\n"
                        + "    <name>Wynton Marsalis</name>\n"
                        + "    <genre>jazz</genre>\n"
                        + "    <alive>\n"
                        + "      <value>1</value>\n"
                        + "    </alive>\n"
                        + "  </musician>\n"
                        + "</list>";

        assertBothWays(jazzIcons, expectedXml);
    }
}
