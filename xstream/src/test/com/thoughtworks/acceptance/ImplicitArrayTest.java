/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. July 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.Arrays;
import java.util.List;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.InitializationException;

/**
 * @author J&ouml;rg Schaible
 */
public class ImplicitArrayTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("farm", Farm.class);
        xstream.alias("animal", Animal.class);
        xstream.alias("MEGA-farm", MegaFarm.class);
    }

    public static class Farm extends StandardObject {
        private transient int idx;
        Animal[] animals;
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
    
    public void testWithDirectType() {
        Farm farm = new Farm();
        farm.animals = new Animal[] {
            new Animal("Cow"),
            new Animal("Sheep")
        };

        String expected = "" +
                "<farm>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "</farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public static class MegaFarm extends Farm {
        String[] names;
    }

    public void testInheritsImplicitArrayFromSuperclass() {
        Farm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[] {
            new Animal("Cow"),
            new Animal("Sheep")
        };

        String expected = "" +
                "<MEGA-farm>\n" +
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

    public void testSupportsInheritedAndDirectDeclaredImplicitArraysAtOnce() {
        MegaFarm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[] {
            new Animal("Cow"),
            new Animal("Sheep")
        };
        farm.names = new String[] {
            "McDonald",
            "Ponte Rosa"
        };
        
        String expected = "" +
                "<MEGA-farm>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "  <name>McDonald</name>\n" +
                "  <name>Ponte Rosa</name>\n" +
                "</MEGA-farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        xstream.addImplicitArray(MegaFarm.class, "names", "name");
        assertBothWays(farm, expected);
    }

    public void testAllowsSubclassToOverrideImplicitCollectionInSuperclass() {
        Farm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[] {
            new Animal("Cow"),
            new Animal("Sheep")
        };

        String expected = "" +
                "<MEGA-farm>\n" +
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
        private Room[] rooms;
        private Person[] people;
        
        public List getPeople() {
            return Arrays.asList(people);
        }
        
        public List getRooms() {
            return Arrays.asList(rooms);
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
        private String[] emailAddresses;

        public Person(String name) {
            this.name = name;
        }
    }

    public void testMultipleArraysBasedOnDifferentType() {
        House house = new House();
        house.rooms = new Room[] {
            new Room("kitchen"),
            new Room("bathroom")
        };
        Person joe = new Person("joe");
        joe.emailAddresses = new String[]{
            "joe@house.org",
            "joe.farmer@house.org"
        };
        Person jaimie = new Person("jaimie");
        jaimie.emailAddresses = new String[]{
            "jaimie@house.org",
            "jaimie.farmer@house.org",
            "jaimie.ann.farmer@house.org"
        };
        house.people = new Person[]{
            joe,
            jaimie
        };

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

        xstream.alias("room", Room.class);
        xstream.alias("house", House.class);
        xstream.alias("person", Person.class);
        xstream.addImplicitArray(House.class, "rooms");
        xstream.addImplicitArray(House.class, "people");
        xstream.addImplicitArray(Person.class, "emailAddresses", "email");

        House serializedHouse = (House)assertBothWays(house, expected);
        assertEquals(house.getPeople(), serializedHouse.getPeople());
        assertEquals(house.getRooms(), serializedHouse.getRooms());
    }

    public static class NumberedRoom extends Room {
        private int number;

        public NumberedRoom(int number) {
            super("room");
            this.number = number;
        }
    }

    public void testArraysWithDerivedElements() {
        House house = new House();
        house.rooms = new Room[] {
            new Room("kitchen"),
            new NumberedRoom(13)
        };

        String expected = ""
                + "<house>\n"
                + "  <room>\n"
                + "    <name>kitchen</name>\n"
                + "  </room>\n"
                + "  <room class=\"chamber\" number=\"13\">\n"
                + "    <name>room</name>\n"
                + "  </room>\n"
                + "</house>";

        xstream.alias("house", House.class);
        xstream.alias("chamber", NumberedRoom.class);
        xstream.addImplicitArray(House.class, "rooms", "room");
        xstream.useAttributeFor(int.class);

        House serializedHouse = (House)assertBothWays(house, expected);
        assertEquals(house.getRooms(), serializedHouse.getRooms());
    }

    public static class Aquarium extends StandardObject {
        private String name;
        private String[] fish;

        public Aquarium(String name) {
            this.name = name;
        }
    }

    public void testWithExplicitItemNameMatchingTheNameOfTheFieldWithTheArray() {
        Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{
            "salmon",
            "halibut",
            "snapper"
        };

        String expected = "" +
                "<aquarium>\n" +
                "  <name>hatchery</name>\n" +
                "  <fish>salmon</fish>\n" +
                "  <fish>halibut</fish>\n" +
                "  <fish>snapper</fish>\n" +
                "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.addImplicitArray(Aquarium.class, "fish", "fish");

        assertBothWays(aquarium, expected);
    }
    
    public void testWithImplicitNameMatchingTheNameOfTheFieldWithTheArray() {
        Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{
            "salmon",
            "halibut",
            "snapper"
        };

        String expected = "" +
                "<aquarium>\n" +
                "  <name>hatchery</name>\n" +
                "  <fish>salmon</fish>\n" +
                "  <fish>halibut</fish>\n" +
                "  <fish>snapper</fish>\n" +
                "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.alias("fish", String.class);
        xstream.addImplicitArray(Aquarium.class, "fish");

        assertBothWays(aquarium, expected);
    }
    
    public void testWithAliasedItemNameMatchingTheAliasedNameOfTheFieldWithTheArray() {
        Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{
            "salmon",
            "halibut",
            "snapper"
        };

        String expected = "" +
                "<aquarium>\n" +
                "  <name>hatchery</name>\n" +
                "  <animal>salmon</animal>\n" +
                "  <animal>halibut</animal>\n" +
                "  <animal>snapper</animal>\n" +
                "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.aliasField("animal", Aquarium.class, "fish");
        xstream.addImplicitArray(Aquarium.class, "fish", "animal");

        assertBothWays(aquarium, expected);
    }

    public void testCanBeDeclaredOnlyForMatchingType() {
        try {
            xstream.addImplicitArray(Animal.class, "name");
            fail("Thrown " + InitializationException.class.getName() + " expected");
        } catch (final InitializationException e) {
            assertTrue(e.getMessage().indexOf("declares no collection") >= 0);
        }
    }

    public void testCanBeDeclaredOnlyForMatchingComponentType() {
        try {
            xstream.addImplicitArray(Aquarium.class, "fish", Farm.class);
            fail("Thrown " + InitializationException.class.getName() + " expected");
        } catch (final InitializationException e) {
            assertTrue(e.getMessage().indexOf("array type is not compatible") >= 0);
        }
    }

    public void testWithNullElement() {
        Farm farm = new Farm();
        farm.animals = new Animal[] {
            new Animal("Cow"),
            null,
            new Animal("Sheep")
        };

        String expected = "" +
                "<farm>\n" +
                "  <animal>\n" +
                "    <name>Cow</name>\n" +
                "  </animal>\n" +
                "  <null/>\n" +
                "  <animal>\n" +
                "    <name>Sheep</name>\n" +
                "  </animal>\n" +
                "</farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testWithNullElementAnsAlias() {
        Farm farm = new Farm();
        farm.animals = new Animal[] {
            null,
            new Animal("Sheep")
        };

        String expected = "" +
                "<farm>\n" +
                "  <null/>\n" +
                "  <beast>\n" +
                "    <name>Sheep</name>\n" +
                "  </beast>\n" +
                "</farm>";

        xstream.addImplicitArray(Farm.class, "animals", "beast");
        assertBothWays(farm, expected);
    }
    
    static class PrimitiveArray {
        int[] ints; 
    };

    public void testWithPrimitiveArray() {
        PrimitiveArray pa = new PrimitiveArray();
        pa.ints = new int[]{ 47, 11 };

        String expected = "" +
                "<primitives>\n" +
                "  <int>47</int>\n" +
                "  <int>11</int>\n" +
                "</primitives>";

        xstream.alias("primitives", PrimitiveArray.class);
        xstream.addImplicitArray(PrimitiveArray.class, "ints");
        assertBothWays(pa, expected);
    }

    static class MultiDimenstionalArrays {
        Object[][] multiObject; 
        String[][] multiString; 
        int[][] multiInt; 
    };

    public void testMultiDimensionalDirectArray() {
        MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiString = new String[][]{ 
            new String[]{ "1", "2" }, 
            new String[]{ "a", "b", "c" }
        };

        String expected = "" +
                "<N>\n" +
                "  <string-array>\n" +
                "    <string>1</string>\n" +
                "    <string>2</string>\n" +
                "  </string-array>\n" +
                "  <string-array>\n" +
                "    <string>a</string>\n" +
                "    <string>b</string>\n" +
                "    <string>c</string>\n" +
                "  </string-array>\n" +
                "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiString");
        assertBothWays(multiDim, expected);
    }

    public void testMultiDimensionalPrimitiveArray() {
        MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiInt = new int[][]{ 
            new int[]{ 1, 2 }, 
            new int[]{ 0, -1, -2 }
        };

        String expected = "" +
                "<N>\n" +
                "  <int-array>\n" +
                "    <int>1</int>\n" +
                "    <int>2</int>\n" +
                "  </int-array>\n" +
                "  <int-array>\n" +
                "    <int>0</int>\n" +
                "    <int>-1</int>\n" +
                "    <int>-2</int>\n" +
                "  </int-array>\n" +
                "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiInt");
        assertBothWays(multiDim, expected);
    }

    public void testMultiDimensionalArrayWithAlias() {
        MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiObject = new Object[][]{ 
            new String[]{ "1" }, 
            new Object[]{ "a", Boolean.FALSE }
        };

        String expected = "" +
                "<N>\n" +
                "  <M class=\"string-array\">\n" +
                "    <string>1</string>\n" +
                "  </M>\n" +
                "  <M>\n" +
                "    <string>a</string>\n" +
                "    <boolean>false</boolean>\n" +
                "  </M>\n" +
                "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiObject", "M");
        assertBothWays(multiDim, expected);
    }
}
