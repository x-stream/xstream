package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.List;

public class DefaultCollectionTest extends AbstractAcceptanceTest {

    public static class Farm extends StandardObject {
        int size;
        List animals = new ArrayList();

        public Farm(int size) {
            this.size = size;
        }

        public void add(Animal animal) {
            animals.add(animal);
        }
    }

    public static class Animal extends StandardObject {
        String name;

        public Animal(String name) {
            this.name = name;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("farm", Farm.class);
        xstream.alias("animal", Animal.class);
    }

    public void testWithout() {
        Farm farm = new Farm(100);
        farm.add(new Animal("Cow"));
        farm.add(new Animal("Sheep"));

        String expected = "" +
                "<farm>\n" +
                "  <size>100</size>\n" +
                "  <animals>\n" +
                "    <animal>\n" +
                "      <name>Cow</name>\n" +
                "    </animal>\n" +
                "    <animal>\n" +
                "      <name>Sheep</name>\n" +
                "    </animal>\n" +
                "  </animals>\n" +
                "</farm>";

        assertBothWays(farm, expected);
    }

    public void testWith() {
        Farm farm = new Farm(100);
        farm.add(new Animal("Cow"));
        farm.add(new Animal("Sheep"));

        String expected = "" +
                "<farm>\n" +
                "  <size>100</size>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "</farm>";

        xstream.addImplicitCollection(Farm.class, "animals");
        assertBothWays(farm, expected);
    }
}
