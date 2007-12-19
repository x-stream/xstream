/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. July 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DefaultImplementationTest extends AbstractAcceptanceTest {


    public static class Farm extends StandardObject {
        int size;
        List animals = new ArrayList();
        String name;

        public Farm(int size, String name) {
            this.size = size;
            this.name = name;
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
        TimeZoneChanger.change("GMT");
        xstream.alias("farm", Farm.class);
        xstream.alias("animal", Animal.class);
        xstream.alias("age", Age.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testArrayList() {
        Farm farm = new Farm(100, "Old McDonald's");
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
                "  <name>Old McDonald&apos;s</name>\n" +
                "</farm>";

        assertBothWays(farm, expected);
    }

    public static class Age extends StandardObject {
        java.util.Date date;

        public Age(java.util.Date age) {
            this.date = age;
        }
    }

    public void testCustomDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2007, Calendar.DECEMBER, 18);
        Age age = new Age(new java.sql.Date(cal.getTime().getTime()));

        xstream.addDefaultImplementation(java.sql.Date.class, java.util.Date.class);
        
        String expected = "" +
                "<age>\n" +
                "  <date>2007-12-18</date>\n" +
                "</age>";

        assertBothWays(age, expected);
    }
}
