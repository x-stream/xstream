package com.thoughtworks.acceptance.annotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Simple tests for class annotations
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 */
public class AnnotationsTest extends AbstractAcceptanceTest {

    public void testAnnotations() {
        Annotations.configureAliases(xstream, Person.class, AddressBookInfo.class);
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

    public void testUsesClassLevelAliasesAnnotationsUsingTwoXStreamInstances() {
        Annotations.configureAliases(xstream, Person.class, AddressBookInfo.class);
        Person person = new Person("john doe");
        String xml = "<person>john doe</person>";
        assertBothWays(person, xml);
        xstream = createXStream();
        Annotations.configureAliases(xstream, Person.class, AddressBookInfo.class);
        assertBothWays(person, xml);
    }

    public void testUsesFieldLevelAliasesAnnotationCycle() {
        Annotations.configureAliases(xstream, Cycle.class);
        Cycle cycle = new Cycle();
        String xml = "<com.thoughtworks.acceptance.annotations.AnnotationsTest_-Cycle/>";
        assertBothWays(cycle, xml);
    }

    public static class Cycle {
        @XStreamAlias("oops")
        private Cycle internal;
    }

    public void testUsesFieldAliasesAnnotations() {
        Annotations.configureAliases(xstream, CustomPerson.class);
        List<String> nickNames = new ArrayList<String>();
        nickNames.add("johnny");
        nickNames.add("jack");
        CustomPerson person = new CustomPerson("john", "doe", 25, nickNames);
        String expectedXml = "<person>\n"
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

    public static class House {
        @XStreamAlias("total-number-of-rooms")
        private int rooms;

        protected House(int rooms) {
            this.rooms = rooms;
        }

        public int getRooms() {
            return rooms;
        }
    }

    public void testAnnotationForFieldAliases() {
        Annotations.configureAliases(xstream, House.class);
        House house = new House(5);
        String expectedXml = ""
            + "<com.thoughtworks.acceptance.annotations.AnnotationsTest_-House>\n"
            + "  <total-number-of-rooms>5</total-number-of-rooms>\n"
            + "</com.thoughtworks.acceptance.annotations.AnnotationsTest_-House>";
        assertBothWays(house, expectedXml);
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

    @XStreamAlias("param")
    public static class ParameterizedContainer {

        private ParameterizedType<InternalType> type;

        public ParameterizedContainer() {
            type = new ParameterizedType<InternalType>(new InternalType());
        }

    }

    @XStreamAlias("param")
    public static class DoubleParameterizedContainer {

        private ArrayList<ArrayList<InternalType>> list;

        public DoubleParameterizedContainer() {
            list = new ArrayList<ArrayList<InternalType>>();
            list.add(new ArrayList<InternalType>());
            list.get(0).add(new InternalType());
        }

    }

    @XStreamAlias("internal")
    public static class InternalParameterizedType {
        @XStreamImplicit(itemFieldName = "line")
        private ArrayList<ArrayList<Point>> signatureLines;
    }

    @XStreamAlias("point")
    public static class Point {
        @XStreamAsAttribute
        private int x;
        @XStreamAsAttribute
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void testHandlesInternalParameterizedTypes() {
        Annotations.configureAliases(xstream, InternalParameterizedType.class);
        Annotations.configureAliases(xstream, Point.class);
        String xml = ""
            + "<internal>\n"
            + "  <line>\n"
            + "    <point x=\"33\" y=\"11\"/>\n"
            + "  </line>\n"
            + "</internal>";
        InternalParameterizedType root = new InternalParameterizedType();
        root.signatureLines = new ArrayList<ArrayList<Point>>();
        root.signatureLines.add(new ArrayList<Point>());
        root.signatureLines.get(0).add(new Point(33, 11));
        assertBothWays(root, xml);
    }

    @XStreamAlias("second")
    public static class InternalType {
        @XStreamAlias("aliased")
        private String original = "value";
    }

    public static class ParameterizedType<T> {
        @XStreamAlias("fieldAlias")
        private T object;

        public ParameterizedType(T object) {
            this.object = object;
        }
    }

    public void testCrawlsWhithinAnnotatedParameterizedTypes() {
        Annotations.configureAliases(xstream, ParameterizedContainer.class);
        String xml = ""
            + "<param>\n"
            + "  <type>\n"
            + "    <fieldAlias class=\"second\">\n"
            + "      <aliased>value</aliased>\n"
            + "    </fieldAlias>\n"
            + "  </type>\n"
            + "</param>";
        assertBothWays(new ParameterizedContainer(), xml);
    }

    public void testCrawlsWhithinAnnotatedDoubleParameterizedTypes() {
        Annotations.configureAliases(xstream, DoubleParameterizedContainer.class);
        String xml = ""
            + "<param>\n"
            + "  <list>\n"
            + "    <list>\n"
            + "      <second>\n"
            + "        <aliased>value</aliased>\n"
            + "      </second>\n"
            + "    </list>\n"
            + "  </list>\n"
            + "</param>";
        assertBothWays(new DoubleParameterizedContainer(), xml);
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

    @XStreamAlias("annotated")
    public static class AnnotatedAttribute {
        @XStreamAsAttribute
        private String myField;
    }

    public void testUsesAttributeThroughAnnotation() {
        AnnotatedAttribute value = new AnnotatedAttribute();
        value.myField = "hello";
        String expected = "<annotated myField=\"hello\"/>";
        Annotations.configureAliases(xstream, AnnotatedAttribute.class);
        String xml = toXML(value);
        AnnotatedAttribute an = (AnnotatedAttribute)xstream.fromXML(xml);
        assertBothWays(value, expected);
    }

    @XStreamAlias("annotated")
    public static class AnnotatedAliasedAttribute {
        @XStreamAsAttribute
        @XStreamAlias("field")
        private String myField;
    }

    public void testUsesAliasedAttributeThroughAnnotation() {
        AnnotatedAliasedAttribute value = new AnnotatedAliasedAttribute();
        value.myField = "hello";
        String expected = "<annotated field=\"hello\"/>";
        Annotations.configureAliases(xstream, AnnotatedAliasedAttribute.class);
        String xml = toXML(value);
        AnnotatedAliasedAttribute an = (AnnotatedAliasedAttribute)xstream.fromXML(xml);
        assertBothWays(value, expected);
    }

    public static class Apartment {

        @XStreamOmitField
        int size;

        protected Apartment(int size) {
            this.size = size;
        }
    }

    public void testIgnoresFieldWhenUsingTheOmitFieldAnnotation() {
        Annotations.configureAliases(xstream, Apartment.class);
        Apartment ap = new Apartment(5);
        String expectedXml = "<com.thoughtworks.acceptance.annotations.AnnotationsTest_-Apartment/>";
        assertBothWays(ap, expectedXml);
    }
}
