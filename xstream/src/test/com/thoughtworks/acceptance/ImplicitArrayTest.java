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
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.InitializationException;


/**
 * @author J&ouml;rg Schaible
 */
public class ImplicitArrayTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("farm", Farm.class);
        xstream.alias("animal", Animal.class);
        xstream.alias("dog", Dog.class);
        xstream.alias("cat", Cat.class);
        xstream.alias("MEGA-farm", MegaFarm.class);
        xstream.alias("area", Area.class);
        xstream.alias("country", Country.class);
        xstream.ignoreUnknownElements();
    }

    public static class Farm extends StandardObject {
        private static final long serialVersionUID = 201107L;
        @SuppressWarnings("unused")
        private transient int idx;
        Animal[] animals;
    }

    public static class Animal extends StandardObject {
        private static final long serialVersionUID = 201107L;
        String name;

        public Animal(final String name) {
            this.name = name;
        }
    }

    public void testWithDirectType() {
        final Farm farm = new Farm();
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};

        final String expected = ""
            + "<farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "</farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testWithReferencedImplicitElement() {
        final List<Object> list = new ArrayList<>();
        final Farm farm = new Farm();
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};
        list.add(farm.animals[0]);
        list.add(farm);
        list.add(farm.animals[1]);

        final String expected = ""
            + "<list>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <farm>\n"
            + "    <animal reference=\"../../animal\"/>\n"
            + "    <animal>\n"
            + "      <name>Sheep</name>\n"
            + "    </animal>\n"
            + "  </farm>\n"
            + "  <animal reference=\"../farm/animal[2]\"/>\n"
            + "</list>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(list, expected);
    }

    public static class MegaFarm extends Farm {
        private static final long serialVersionUID = 201107L;
        String separator = "---";
        String[] names;
    }

    public void testInheritsImplicitArrayFromSuperclass() {
        final Farm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};

        final String expected = ""
            + "<MEGA-farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <separator>---</separator>\n"
            + "</MEGA-farm>";

        xstream.addImplicitCollection(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testSupportsInheritedAndDirectDeclaredImplicitArraysAtOnce() {
        final MegaFarm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};
        farm.names = new String[]{"McDonald", "Ponte Rosa"};

        final String expected = ""
            + "<MEGA-farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <separator>---</separator>\n"
            + "  <name>McDonald</name>\n"
            + "  <name>Ponte Rosa</name>\n"
            + "</MEGA-farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        xstream.addImplicitArray(MegaFarm.class, "names", "name");
        assertBothWays(farm, expected);
    }

    public void testInheritedAndDirectDeclaredImplicitArraysAtOnceIsNotDeclarationSequenceDependent() {
        final MegaFarm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};
        farm.names = new String[]{"McDonald", "Ponte Rosa"};

        final String expected = ""
            + "<MEGA-farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <separator>---</separator>\n"
            + "  <name>McDonald</name>\n"
            + "  <name>Ponte Rosa</name>\n"
            + "</MEGA-farm>";

        xstream.addImplicitArray(MegaFarm.class, "names", "name");
        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testAllowsSubclassToOverrideImplicitCollectionInSuperclass() {
        final Farm farm = new MegaFarm(); // subclass
        farm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};

        final String expected = ""
            + "<MEGA-farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <separator>---</separator>\n"
            + "</MEGA-farm>";

        xstream.addImplicitCollection(MegaFarm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testAllowDifferentImplicitArrayDefinitionsInSubclass() {
        final Farm farm = new Farm();
        farm.animals = new Animal[]{new Animal("Cod"), new Animal("Salmon")};
        final MegaFarm megaFarm = new MegaFarm(); // subclass
        megaFarm.animals = new Animal[]{new Animal("Cow"), new Animal("Sheep")};
        megaFarm.names = new String[]{"McDonald", "Ponte Rosa"};

        final List<Farm> list = new ArrayList<>();
        list.add(farm);
        list.add(megaFarm);
        final String expected = ""
            + "<list>\n"
            + "  <farm>\n"
            + "    <fish>\n"
            + "      <name>Cod</name>\n"
            + "    </fish>\n"
            + "    <fish>\n"
            + "      <name>Salmon</name>\n"
            + "    </fish>\n"
            + "  </farm>\n"
            + "  <MEGA-farm>\n"
            + "    <animal>\n"
            + "      <name>Cow</name>\n"
            + "    </animal>\n"
            + "    <animal>\n"
            + "      <name>Sheep</name>\n"
            + "    </animal>\n"
            + "    <separator>---</separator>\n"
            + "    <name>McDonald</name>\n"
            + "    <name>Ponte Rosa</name>\n"
            + "  </MEGA-farm>\n"
            + "</list>";

        xstream.addImplicitArray(Farm.class, "animals", "fish");
        xstream.addImplicitArray(MegaFarm.class, "animals");
        xstream.addImplicitArray(MegaFarm.class, "names", "name");
        assertBothWays(list, expected);
    }

    public static class House extends StandardObject {
        private static final long serialVersionUID = 201107L;
        private Room[] rooms;
        @SuppressWarnings("unused")
        private final String separator = "---";
        private Person[] people;

        public List<Person> getPeople() {
            return Arrays.asList(people);
        }

        public List<Room> getRooms() {
            return Arrays.asList(rooms);
        }
    }

    public static class Room extends StandardObject {
        private static final long serialVersionUID = 201107L;
        final String name;

        public Room(final String name) {
            this.name = name;
        }
    }

    public static class Person extends StandardObject {
        private static final long serialVersionUID = 201107L;
        final String name;
        String[] emailAddresses;

        public Person(final String name) {
            this.name = name;
        }
    }

    public void testMultipleArraysBasedOnDifferentType() {
        final House house = new House();
        house.rooms = new Room[]{new Room("kitchen"), new Room("bathroom")};
        final Person joe = new Person("joe");
        joe.emailAddresses = new String[]{"joe@house.org", "joe.farmer@house.org"};
        final Person jaimie = new Person("jaimie");
        jaimie.emailAddresses = new String[]{
            "jaimie@house.org", "jaimie.farmer@house.org", "jaimie.ann.farmer@house.org"};
        house.people = new Person[]{joe, jaimie};

        final String expected = ""
            + "<house>\n"
            + "  <room>\n"
            + "    <name>kitchen</name>\n"
            + "  </room>\n"
            + "  <room>\n"
            + "    <name>bathroom</name>\n"
            + "  </room>\n"
            + "  <separator>---</separator>\n"
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

        final House serializedHouse = assertBothWays(house, expected);
        assertEquals(house.getPeople(), serializedHouse.getPeople());
        assertEquals(house.getRooms(), serializedHouse.getRooms());
    }

    public static class NumberedRoom extends Room {
        private static final long serialVersionUID = 201107L;
        final int number;

        public NumberedRoom(final int number) {
            super("room");
            this.number = number;
        }
    }

    public void testArraysWithDerivedElements() {
        final House house = new House();
        house.rooms = new Room[]{new Room("kitchen"), new NumberedRoom(13)};

        final String expected = ""
            + "<house>\n"
            + "  <room>\n"
            + "    <name>kitchen</name>\n"
            + "  </room>\n"
            + "  <room class=\"chamber\" number=\"13\">\n"
            + "    <name>room</name>\n"
            + "  </room>\n"
            + "  <separator>---</separator>\n"
            + "</house>";

        xstream.alias("house", House.class);
        xstream.alias("chamber", NumberedRoom.class);
        xstream.addImplicitArray(House.class, "rooms", "room");
        xstream.useAttributeFor(int.class);

        final House serializedHouse = assertBothWays(house, expected);
        assertEquals(house.getRooms(), serializedHouse.getRooms());
    }

    public static class Aquarium extends StandardObject {
        private static final long serialVersionUID = 201107L;
        final String name;
        String[] fish;

        public Aquarium(final String name) {
            this.name = name;
        }
    }

    public void testWithExplicitItemNameMatchingTheNameOfTheFieldWithTheArray() {
        final Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{"salmon", "halibut", "snapper"};

        final String expected = ""
            + "<aquarium>\n"
            + "  <name>hatchery</name>\n"
            + "  <fish>salmon</fish>\n"
            + "  <fish>halibut</fish>\n"
            + "  <fish>snapper</fish>\n"
            + "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.addImplicitArray(Aquarium.class, "fish", "fish");

        assertBothWays(aquarium, expected);
    }

    public void testWithImplicitNameMatchingTheNameOfTheFieldWithTheArray() {
        final Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{"salmon", "halibut", "snapper"};

        final String expected = ""
            + "<aquarium>\n"
            + "  <name>hatchery</name>\n"
            + "  <fish>salmon</fish>\n"
            + "  <fish>halibut</fish>\n"
            + "  <fish>snapper</fish>\n"
            + "</aquarium>";

        xstream.alias("aquarium", Aquarium.class);
        xstream.alias("fish", String.class);
        xstream.addImplicitArray(Aquarium.class, "fish");

        assertBothWays(aquarium, expected);
    }

    public void testWithAliasedItemNameMatchingTheAliasedNameOfTheFieldWithTheArray() {
        final Aquarium aquarium = new Aquarium("hatchery");
        aquarium.fish = new String[]{"salmon", "halibut", "snapper"};

        final String expected = ""
            + "<aquarium>\n"
            + "  <name>hatchery</name>\n"
            + "  <animal>salmon</animal>\n"
            + "  <animal>halibut</animal>\n"
            + "  <animal>snapper</animal>\n"
            + "</aquarium>";

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
            assertTrue(e.getMessage().contains("declares no collection"));
        }
    }

    public void testCanBeDeclaredOnlyForMatchingComponentType() {
        try {
            xstream.addImplicitArray(Aquarium.class, "fish", Farm.class);
            fail("Thrown " + InitializationException.class.getName() + " expected");
        } catch (final InitializationException e) {
            assertTrue(e.getMessage().contains("array type is not compatible"));
        }
    }

    public void testWithNullElement() {
        final Farm farm = new Farm();
        farm.animals = new Animal[]{new Animal("Cow"), null, new Animal("Sheep")};

        final String expected = ""
            + "<farm>\n"
            + "  <animal>\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <null/>\n"
            + "  <animal>\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "</farm>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    public void testWithAliasAndNullElement() {
        final Farm farm = new Farm();
        farm.animals = new Animal[]{null, new Animal("Sheep")};

        final String expected = ""
            + "<farm>\n"
            + "  <null/>\n"
            + "  <beast>\n"
            + "    <name>Sheep</name>\n"
            + "  </beast>\n"
            + "</farm>";

        xstream.addImplicitArray(Farm.class, "animals", "beast");
        assertBothWays(farm, expected);
    }

    public static class Area extends Farm {
        private static final long serialVersionUID = 201509L;
        @SuppressWarnings("hiding")
        Animal[] animals;
    }

    public void testWithHiddenArray() {
        final Area area = new Area();
        ((Farm)area).animals = new Animal[2];
        ((Farm)area).animals[0] = new Animal("Cow");
        ((Farm)area).animals[1] = new Animal("Sheep");
        area.animals = new Animal[2];
        area.animals[0] = new Animal("Falcon");
        area.animals[1] = new Animal("Sparrow");

        final String expected = ""
            + "<area>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Falcon</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sparrow</name>\n"
            + "  </animal>\n"
            + "</area>";

        xstream.addImplicitArray(Farm.class, "animals");
        xstream.addImplicitArray(Area.class, "animals");
        assertBothWays(area, expected);
    }

    public void testWithHiddenArrayAndDifferentAlias() {
        final Area area = new Area();
        ((Farm)area).animals = new Animal[2];
        ((Farm)area).animals[0] = new Animal("Cow");
        ((Farm)area).animals[1] = new Animal("Sheep");
        area.animals = new Animal[2];
        area.animals[0] = new Animal("Falcon");
        area.animals[1] = new Animal("Sparrow");

        final String expected = ""
            + "<area>\n"
            + "  <domesticated defined-in=\"farm\">\n"
            + "    <name>Cow</name>\n"
            + "  </domesticated>\n"
            + "  <domesticated defined-in=\"farm\">\n"
            + "    <name>Sheep</name>\n"
            + "  </domesticated>\n"
            + "  <wild>\n"
            + "    <name>Falcon</name>\n"
            + "  </wild>\n"
            + "  <wild>\n"
            + "    <name>Sparrow</name>\n"
            + "  </wild>\n"
            + "</area>";

        xstream.addImplicitArray(Farm.class, "animals", "domesticated");
        xstream.addImplicitArray(Area.class, "animals", "wild");
        assertBothWays(area, expected);
    }

    public void testDoesNotInheritFromHiddenArrayOfSuperclass() {
        final Area area = new Area();
        ((Farm)area).animals = new Animal[2];
        ((Farm)area).animals[0] = new Animal("Cow");
        ((Farm)area).animals[1] = new Animal("Sheep");
        area.animals = new Animal[2];
        area.animals[0] = new Animal("Falcon");
        area.animals[1] = new Animal("Sparrow");

        final String expected = ""
            + "<area>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <animals>\n"
            + "    <animal>\n"
            + "      <name>Falcon</name>\n"
            + "    </animal>\n"
            + "    <animal>\n"
            + "      <name>Sparrow</name>\n"
            + "    </animal>\n"
            + "  </animals>\n"
            + "</area>";

        xstream.addImplicitArray(Farm.class, "animals");
        assertBothWays(area, expected);
    }

    public void testDoesNotPropagateToHiddenArrayOfSuperclass() {
        final Area area = new Area();
        ((Farm)area).animals = new Animal[2];
        ((Farm)area).animals[0] = new Animal("Cow");
        ((Farm)area).animals[1] = new Animal("Sheep");
        area.animals = new Animal[2];
        area.animals[0] = new Animal("Falcon");
        area.animals[1] = new Animal("Sparrow");

        final String expected = ""
            + "<area>\n"
            + "  <animals defined-in=\"farm\">\n"
            + "    <animal>\n"
            + "      <name>Cow</name>\n"
            + "    </animal>\n"
            + "    <animal>\n"
            + "      <name>Sheep</name>\n"
            + "    </animal>\n"
            + "  </animals>\n"
            + "  <animal>\n"
            + "    <name>Falcon</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Sparrow</name>\n"
            + "  </animal>\n"
            + "</area>";

        xstream.addImplicitArray(Area.class, "animals");
        assertBothWays(area, expected);
    }

    public static class County extends Area {
        private static final long serialVersionUID = 201509L;
    }

    public static class Country extends County {
        private static final long serialVersionUID = 201509L;
        @SuppressWarnings("hiding")
        Animal[] animals;
    }

    public void testWithDoubleHiddenArray() {
        final Country country = new Country();
        ((Farm)country).animals = new Animal[2];
        ((Farm)country).animals[0] = new Animal("Cow");
        ((Farm)country).animals[1] = new Animal("Sheep");
        ((Area)country).animals = new Animal[2];
        ((Area)country).animals[0] = new Animal("Falcon");
        ((Area)country).animals[1] = new Animal("Sparrow");
        country.animals = new Animal[2];
        country.animals[0] = new Animal("Wale");
        country.animals[1] = new Animal("Dolphin");

        final String expected = ""
            + "<country>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Cow</name>\n"
            + "  </animal>\n"
            + "  <animal defined-in=\"farm\">\n"
            + "    <name>Sheep</name>\n"
            + "  </animal>\n"
            + "  <animal defined-in=\"area\">\n"
            + "    <name>Falcon</name>\n"
            + "  </animal>\n"
            + "  <animal defined-in=\"area\">\n"
            + "    <name>Sparrow</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Wale</name>\n"
            + "  </animal>\n"
            + "  <animal>\n"
            + "    <name>Dolphin</name>\n"
            + "  </animal>\n"
            + "</country>";

        xstream.addImplicitArray(Farm.class, "animals");
        xstream.addImplicitArray(Area.class, "animals");
        xstream.addImplicitArray(Country.class, "animals");
        assertBothWays(country, expected);
    }

    public static class Dog extends Animal {
        private static final long serialVersionUID = 201703L;

        public Dog(final String name) {
            super(name);
        }
    }

    public static class Cat extends Animal {
        private static final long serialVersionUID = 201703L;

        public Cat(final String name) {
            super(name);
        }
    }

    public void testCollectsDifferentTypesWithFieldOfSameName() {
        final Farm farm = new Farm();
        farm.animals = new Animal[]{
            new Dog("Lessie"), new Cat("Garfield"), new Cat("Felix"), new Dog("Cujo"), new Cat("Bob")};

        final String expected = ""
            + "<farm>\n"
            + "  <dog>\n"
            + "    <name>Lessie</name>\n"
            + "  </dog>\n"
            + "  <cat>\n"
            + "    <name>Garfield</name>\n"
            + "  </cat>\n"
            + "  <cat>\n"
            + "    <name>Felix</name>\n"
            + "  </cat>\n"
            + "  <dog>\n"
            + "    <name>Cujo</name>\n"
            + "  </dog>\n"
            + "  <cat>\n"
            + "    <name>Bob</name>\n"
            + "  </cat>\n"
            + "</farm>";

        xstream.addImplicitCollection(Farm.class, "animals");
        assertBothWays(farm, expected);
    }

    static class PrimitiveArray {
        int[] ints;
    };

    public void testWithPrimitiveArray() {
        final PrimitiveArray pa = new PrimitiveArray();
        pa.ints = new int[]{47, 11};

        final String expected = "" //
            + "<primitives>\n"
            + "  <int>47</int>\n"
            + "  <int>11</int>\n"
            + "</primitives>";

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
        final MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiString = new String[][]{new String[]{"1", "2"}, new String[]{"a", "b", "c"}};

        final String expected = ""
            + "<N>\n"
            + "  <string-array>\n"
            + "    <string>1</string>\n"
            + "    <string>2</string>\n"
            + "  </string-array>\n"
            + "  <string-array>\n"
            + "    <string>a</string>\n"
            + "    <string>b</string>\n"
            + "    <string>c</string>\n"
            + "  </string-array>\n"
            + "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiString");
        assertBothWays(multiDim, expected);
    }

    public void testMultiDimensionalPrimitiveArray() {
        final MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiInt = new int[][]{new int[]{1, 2}, new int[]{0, -1, -2}};

        final String expected = ""
            + "<N>\n"
            + "  <int-array>\n"
            + "    <int>1</int>\n"
            + "    <int>2</int>\n"
            + "  </int-array>\n"
            + "  <int-array>\n"
            + "    <int>0</int>\n"
            + "    <int>-1</int>\n"
            + "    <int>-2</int>\n"
            + "  </int-array>\n"
            + "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiInt");
        assertBothWays(multiDim, expected);
    }

    public void testMultiDimensionalArrayWithAlias() {
        final MultiDimenstionalArrays multiDim = new MultiDimenstionalArrays();
        multiDim.multiObject = new Object[][]{new String[]{"1"}, new Object[]{"a", Boolean.FALSE}};

        final String expected = ""
            + "<N>\n"
            + "  <M class=\"string-array\">\n"
            + "    <string>1</string>\n"
            + "  </M>\n"
            + "  <M>\n"
            + "    <string>a</string>\n"
            + "    <boolean>false</boolean>\n"
            + "  </M>\n"
            + "</N>";

        xstream.alias("N", MultiDimenstionalArrays.class);
        xstream.addImplicitArray(MultiDimenstionalArrays.class, "multiObject", "M");
        assertBothWays(multiDim, expected);
    }
}
