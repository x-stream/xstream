package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.SampleLists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ImplicitCollectionTest extends AbstractAcceptanceTest {

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

    public static class Animal extends StandardObject implements Comparable {
        String name;

        public Animal(String name) {
            this.name = name;
        }

        public int compareTo(Object o) {
            return name.compareTo(((Animal)o).name);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("zoo", Zoo.class);
        xstream.alias("farm", Farm.class);
        xstream.alias("animal", Animal.class);
        xstream.alias("room", Room.class);
        xstream.alias("house", House.class);
        xstream.alias("person", Person.class);
        xstream.alias("sample", SampleLists.class);
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

    public void testWithList() {
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

    public static class MegaFarm extends Farm {
        public MegaFarm(int size) {
            super(size);
        }
    }

    public void testInheritsImplicitCollectionFromSuperclass() {
        xstream.alias("MEGA-farm", MegaFarm.class);

        Farm farm = new MegaFarm(100); // subclass
        farm.add(new Animal("Cow"));
        farm.add(new Animal("Sheep"));

        String expected = "" +
                "<MEGA-farm>\n" +
                "  <size>100</size>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "</MEGA-farm>";

        xstream.addImplicitCollection(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testAllowsSubclassToOverrideImplicitCollectionInSuperclass() {
        xstream.alias("MEGA-farm", MegaFarm.class);

        Farm farm = new MegaFarm(100); // subclass
        farm.add(new Animal("Cow"));
        farm.add(new Animal("Sheep"));

        String expected = "" +
                "<MEGA-farm>\n" +
                "  <size>100</size>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "</MEGA-farm>";

        xstream.addImplicitCollection(MegaFarm.class, "animals");
        assertBothWays(farm, expected);
    }

    public static class House extends StandardObject {
        private List rooms = new ArrayList();
        private List people = new ArrayList();

        public void add(Room room) {
            rooms.add(room);
        }

        public void add(Person person) {
            people.add(person);
        }
    }

    public static class Room extends StandardObject {
        private String name;

        public Room(String name) {
            this.name = name;
        }
    }

    public static class Person extends StandardObject {
        private String name;
        private LinkedList emailAddresses = new LinkedList();

        public Person(String name) {
            this.name = name;
        }

        public void addEmailAddress(String email) {
            emailAddresses.add(email);
        }
    }

    public void testDefaultCollectionBasedOnType() {
        House house = new House();
        house.add(new Room("kitchen"));
        house.add(new Room("bathroom"));
        Person joe = new Person("joe");
        joe.addEmailAddress("joe@house.org");
        joe.addEmailAddress("joe.farmer@house.org");
        house.add(joe);
        Person jaimie = new Person("jaimie");
        jaimie.addEmailAddress("jaimie@house.org");
        jaimie.addEmailAddress("jaimie.farmer@house.org");
        jaimie.addEmailAddress("jaimie.ann.farmer@house.org");
        house.add(jaimie);

        String expected = ""
                + "<house>\n"
                + "  <room>\n"
                + "    <name>kitchen</name>\n"
                + "  </room>\n"
                + "  <room>\n"
                + "    <name>bathroom</name>\n"
                + "  </room>\n"
                + "  <person>\n"
                + "    <name>joe</name>\n"
                + "    <email>joe@house.org</email>\n"
                + "    <email>joe.farmer@house.org</email>\n"
                + "  </person>\n"
                + "  <person>\n"
                + "    <name>jaimie</name>\n"
                + "    <email>jaimie@house.org</email>\n"
                + "    <email>jaimie.farmer@house.org</email>\n"
                + "    <email>jaimie.ann.farmer@house.org</email>\n"
                + "  </person>\n"
                + "</house>";

        xstream.addImplicitCollection(House.class, "rooms", Room.class);
        xstream.addImplicitCollection(House.class, "people", Person.class);
        xstream.addImplicitCollection(Person.class, "emailAddresses", "email", String.class);

        assertBothWays(house, expected);
    }

    public static class Zoo extends StandardObject {
        private Set animals = new HashSet();
        public void add(Animal animal) {
            animals.add(animal);
        }
    }

    public void testWithSet() {
        Zoo zoo = new Zoo();
        zoo.add(new Animal("Lion"));
        zoo.add(new Animal("Ape"));

        String expected = "" +
                "<zoo>\n" +
                "  <animal>\n" +
                "    <name>Lion</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Ape</name>\n" +
                "  </animal>\n" +
                "</zoo>";

        xstream.addImplicitCollection(Zoo.class, "animals");
        assertBothWays(zoo, expected);
    }

    public void testWithDifferentDefaultImplementation() {
        String xml = "" +
                "<zoo>\n" +
                "  <animal>\n" +
                "    <name>Lion</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Ape</name>\n" +
                "  </animal>\n" +
                "</zoo>";

        xstream.addImplicitCollection(Zoo.class, "animals");
        xstream.addDefaultImplementation(TreeSet.class, Set.class);
        Zoo zoo = (Zoo)xstream.fromXML(xml);
        assertTrue("Collection was a " + zoo.animals.getClass().getName(), zoo.animals instanceof TreeSet);
    }

    public static class Aquarium extends StandardObject {
        private String name;
        private LinkedList fish = new LinkedList();

        public Aquarium(String name) {
            this.name = name;
        }

        public void addFish(String fish) {
            this.fish.add(fish);
        }
    }

    public void testWithExplicitItemNameMatchingTheNameOfTheFieldWithTheCollection() {
        Aquarium aquarium = new Aquarium("hatchery");
        aquarium.addFish("salmon");
        aquarium.addFish("halibut");
        aquarium.addFish("snapper");

        String expected = "" +
                "<aquarium>\n" +
                "  <name>hatchery</name>\n" +
                "  <fish>salmon</fish>\n" +
                "  <fish>halibut</fish>\n" +
                "  <fish>snapper</fish>\n" +
                "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.addImplicitCollection(Aquarium.class, "fish", "fish", String.class);

        assertBothWays(aquarium, expected);
    }

}
