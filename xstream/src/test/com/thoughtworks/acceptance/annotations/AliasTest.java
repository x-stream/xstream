/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. November 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Tests annotations defining aliases for classes or fields.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class AliasTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        XStream xstream = super.createXStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    public void testAnnotationForClassWithAnnotatedConverter() {
        Map<String, Person> map = new HashMap<String, Person>();
        map.put("first person", new Person("john doe"));
        map.put("second person", new Person("jane doe"));
        String xml = ""
            + "<map>\n"
            + "  <entry>\n"
            + "    <string>second person</string>\n"
            + "    <person>jane doe</person>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <string>first person</string>\n"
            + "    <person>john doe</person>\n"
            + "  </entry>\n"
            + "</map>";
        assertBothWaysNormalized(map, xml, "map", "entry", "string");
    }

    public void testAnnotationForFieldWithAliasCycle() {
        Cycle cycle = new Cycle();
        cycle.internal = cycle;
        String xml = "" // 
            + "<cycle>\n" //
            + "  <oops reference=\"..\"/>\n" //
            + "</cycle>";
        assertBothWays(cycle, xml);
    }

    @XStreamAlias("cycle")
    public static class Cycle {
        @XStreamAlias("oops")
        private Cycle internal;
    }

    public void testAnnotationForField() {
        List<String> nickNames = new ArrayList<String>();
        nickNames.add("johnny");
        nickNames.add("jack");
        CustomPerson person = new CustomPerson("john", "doe", 25, nickNames);
        String expectedXml = ""
            + "<person>\n"
            + "  <first-name>john</first-name>\n"
            + "  <last-name>doe</last-name>\n"
            + "  <age-in-years>25</age-in-years>\n"
            + "  <nick-names>\n"
            + "    <string>johnny</string>\n"
            + "    <string>jack</string>\n"
            + "  </nick-names>\n"
            + "</person>";
        assertBothWays(person, expectedXml);
    }

    @XStreamAlias("person")
    public static class CustomPerson {
        @XStreamAlias("first-name")
        String firstName;
        @XStreamAlias("last-name")
        String lastName;
        @XStreamAlias("age-in-years")
        int ageInYears;
        @XStreamAlias("nick-names")
        List<String> nickNames;

        public CustomPerson(
            String firstName, String lastName, int ageInYears, List<String> nickNames) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.ageInYears = ageInYears;
            this.nickNames = nickNames;
        }

        public boolean equals(Object obj) {
            if ((obj == null) || !(obj instanceof CustomPerson)) return false;
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb
                .append("firstName:")
                .append(firstName)
                .append(",lastName:")
                .append(lastName)
                .append(",ageInYears:")
                .append(ageInYears)
                .append(",nickNames:")
                .append(nickNames);
            return sb.toString();
        }

    }

    @XStreamAlias("person")
    @XStreamConverter(PersonConverter.class)
    public static class Person {
        String name;
        AddressBookInfo addressBook;

        public Person(String name) {
            this.name = name;
            addressBook = new AddressBook();
        }

        public boolean equals(Object obj) {
            if ((obj == null) || !(obj instanceof Person)) return false;
            return addressBook.equals(((Person)obj).addressBook);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("name:").append(name).append("addresbook:").append(addressBook);
            return sb.toString();
        }

    }

    @XStreamAlias(value = "addressbook-info", impl = AddressBook.class)
    public interface AddressBookInfo {
        public List<AddressInfo> getAddresses();

        public void setAddresses(List<AddressInfo> address);
    }

    @XStreamAlias("addressbookAlias")
    public static class AddressBook implements AddressBookInfo {

        // @XStreamContainedType
        private List<AddressInfo> addresses;

        public AddressBook() {
            addresses = new ArrayList<AddressInfo>();
            addresses.add(new Address("Home Address", 111));
            addresses.add(new Address("Office Address", 222));
        }

        public List<AddressInfo> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<AddressInfo> addresses) {
            this.addresses = addresses;

        }

        public boolean equals(Object obj) {
            if ((obj == null) || !(obj instanceof AddressBookInfo)) return false;
            return addresses.containsAll(((AddressBookInfo)obj).getAddresses());
        }

    }

    @XStreamAlias(value = "addressinfoAlias", impl = Address.class)
    public interface AddressInfo {
        public String addr();

        public int zipcode();
    }

    @XStreamAlias(value = "addressAlias")
    public static class Address implements AddressInfo {

        private String addr;
        private int zipcode;

        public Address(String addr, int zipcode) {
            this.addr = addr;
            this.zipcode = zipcode;
        }

        public String addr() {
            return addr;
        }

        public int zipcode() {
            return zipcode;
        }

    }

    public static class PersonConverter implements Converter {
        public PersonConverter() {
        }

        public String toString(Object obj) {
            return ((Person)obj).name;
        }

        public Object fromString(String str) {
            return new Person(str);
        }

        public boolean canConvert(Class type) {
            return type == Person.class;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
            writer.setValue(toString(source));
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return fromString(reader.getValue());
        }
    }

    static class Dash {

        @XStreamAlias("camel-case")
        int camelCase = 5;
    }

    public void testAnnotationForFieldWithAttributeDefinitionForFieldType() {
        xstream.alias("dash", Dash.class);
        xstream.useAttributeFor(int.class);
        String xml = "<dash camel-case=\"5\"/>";
        assertBothWays(new Dash(), xml);
    }

    public static abstract class Aged {
        @XStreamAlias("age")
        @XStreamAsAttribute
        private Integer id;

        Aged(Integer id) {
            this.id = id;
        }
    }

    @XStreamAlias("thing")
    public static class AgedThing extends Aged {

        @XStreamAsAttribute
        private String name;

        AgedThing(String name, Integer id) {
            super(id);
            this.name = name;
        }
    }

    public void testAnnotationIsInheritedTogetherWithAsAttribute() {
        String xml = "<thing age=\"99\" name=\"Name\"/>";
        assertBothWays(new AgedThing("Name", 99), xml);
    }
}
