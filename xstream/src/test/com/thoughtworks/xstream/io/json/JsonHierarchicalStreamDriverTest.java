/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.json;

import java.awt.Color;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.OwnerOfExternalizable;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.acceptance.objects.SomethingExternalizable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;

import junit.framework.TestCase;


/**
 * Some of these test cases are taken from example JSON listed at
 * http://www.json.org/example.html
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class JsonHierarchicalStreamDriverTest extends TestCase {
    protected XStream xstream;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream(createDriver());
    }
    
    protected JsonHierarchicalStreamDriver createDriver() {
        return new JsonHierarchicalStreamDriver();
    }
    
    protected String normalizeExpectation(String expected) {
        return expected.replace('\'', '"');
    }

    public void testDoesNotSupportReader() {
        try {
            new JsonHierarchicalStreamDriver().createReader((Reader)null);
            fail("should have barfed");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    public void testDoesNotSupportInputStream() {
        try {
            new JsonHierarchicalStreamDriver().createReader((InputStream)null);
            fail("should have barfed");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    public void testCanMarshalSimpleTypes() {

        String expected = normalizeExpectation(""
            + "{'innerMessage': {\n"
            + "  'long1': 5,\n"
            + "  'long2': 42,\n"
            + "  'greeting': 'hello',\n"
            + "  'num1': 2,\n"
            + "  'num2': 3,\n"
            + "  'bool': true,\n"
            + "  'bool2': true,\n"
            + "  'char1': 'A',\n"
            + "  'char2': 'B',\n"
            + "  'innerMessage': {\n"
            + "    'long1': 0,\n"
            + "    'greeting': 'bonjour',\n"
            + "    'num1': 3,\n"
            + "    'bool': false,\n"
            + "    'char1': '\\u0000'\n"
            + "  }\n"
            + "}}");

        xstream.alias("innerMessage", Message.class);

        Message message = new Message("hello");
        message.long1 = 5L;
        message.long2 = new Long(42);
        message.num1 = 2;
        message.num2 = new Integer(3);
        message.bool = true;
        message.bool2 = Boolean.TRUE;
        message.char1 = 'A';
        message.char2 = new Character('B');

        Message message2 = new Message("bonjour");
        message2.num1 = 3;

        message.innerMessage = message2;

        assertEquals(expected, xstream.toXML(message));
    }

    public static class Message {
        long long1;
        Long long2;
        String greeting;
        int num1;
        Integer num2;
        boolean bool;
        Boolean bool2;
        char char1;
        Character char2;
        Message innerMessage;

        public Message(String greeting) {
            this.greeting = greeting;
        }
    }

    protected String expectedMenuStart = ""
        + "{'menu': {\n"
        + "  'id': 'file',\n"
        + "  'value': 'File:',\n"
        + "  'popup': {\n"
        + "    'menuitem': [";
    protected String expectedNew = ""
        + "      {\n"
        + "        'value': 'New',\n"
        + "        'onclick': 'CreateNewDoc()'\n"
        + "      }";
    protected String expectedOpen = ""
        + "      {\n"
        + "        'value': 'Open',\n"
        + "        'onclick': 'OpenDoc()'\n"
        + "      }";
    protected String expectedClose = ""
        + "      {\n"
        + "        'value': 'Close',\n"
        + "        'onclick': 'CloseDoc()'\n"
        + "      }";
    protected String expectedMenuEnd = "" + "    ]\n" + "  }\n" + "}}";
    protected String expected = "" //
        + expectedMenuStart
        + "\n"
        + expectedNew
        + ",\n"
        + expectedOpen
        + ",\n"
        + expectedClose
        + "\n" + expectedMenuEnd;

    public void testCanMarshalLists() {

        // This from http://www.json.org/example.html

        xstream.alias("menu", MenuWithList.class);
        xstream.alias("menuitem", MenuItem.class);

        MenuWithList menu = new MenuWithList();

        assertEquals(normalizeExpectation(expected), xstream.toXML(menu));
    }

    public void testCanMarshalArrays() {

        xstream.alias("menu", MenuWithArray.class);
        xstream.alias("menuitem", MenuItem.class);

        MenuWithArray menu = new MenuWithArray();

        assertEquals(normalizeExpectation(expected), xstream.toXML(menu));
    }

    public void testCanMarshalSets() {

        // This from http://www.json.org/example.html

        xstream.alias("menu", MenuWithSet.class);
        xstream.alias("menuitem", MenuItem.class);

        MenuWithSet menu = new MenuWithSet();

        String json = xstream.toXML(menu);
        assertTrue(json.startsWith(normalizeExpectation(expectedMenuStart)));
        assertTrue(json.indexOf(expectedNew.replace('\'', '"')) > 0);
        assertTrue(json.indexOf(expectedOpen.replace('\'', '"')) > 0);
        assertTrue(json.indexOf(expectedClose.replace('\'', '"')) > 0);
        assertTrue(json.endsWith(expectedMenuEnd.replace('\'', '"')));
    }

    public static class MenuWithList {
        String id = "file";
        String value = "File:";
        PopupWithList popup = new PopupWithList();
    }

    public static class PopupWithList {
        List menuitem;
        {
            menuitem = new ArrayList();
            menuitem.add(new MenuItem("New", "CreateNewDoc()"));
            menuitem.add(new MenuItem("Open", "OpenDoc()"));
            menuitem.add(new MenuItem("Close", "CloseDoc()"));
        }
    }

    public static class MenuWithArray {
        String id = "file";
        String value = "File:";
        PopupWithArray popup = new PopupWithArray();
    }

    public static class PopupWithArray {
        MenuItem[] menuitem = new MenuItem[]{
            new MenuItem("New", "CreateNewDoc()"), new MenuItem("Open", "OpenDoc()"),
            new MenuItem("Close", "CloseDoc()")};
    }

    public static class MenuWithSet {
        String id = "file";
        String value = "File:";
        PopupWithSet popup = new PopupWithSet();
    }

    public static class PopupWithSet {
        Set menuitem;
        {
            menuitem = new HashSet();
            menuitem.add(new MenuItem("New", "CreateNewDoc()"));
            menuitem.add(new MenuItem("Open", "OpenDoc()"));
            menuitem.add(new MenuItem("Close", "CloseDoc()"));
        }

    }

    public static class MenuItem {
        public String value; // assume unique
        public String onclick;

        public MenuItem(String value, String onclick) {
            this.value = value;
            this.onclick = onclick;
        }

        public int hashCode() {
            return value.hashCode();
        }

    }

    public void testCanMarshalTypesWithPrimitives() {

        // This also from http://www.expected.org/example.html

        String expected = normalizeExpectation("" // 
            + "{'widget': {\n"
            + "  'debug': 'on',\n"
            + "  'window': {\n"
            + "    'title': 'Sample Konfabulator Widget',\n"
            + "    'name': 'main_window',\n"
            + "    'width': 500,\n"
            + "    'height': 500\n"
            + "  },\n"
            + "  'image': {\n"
            + "    'src': 'Images/Sun.png',\n"
            + "    'name': 'sun1',\n"
            + "    'hOffset': 250,\n"
            + "    'vOffset': 250,\n"
            + "    'alignment': 'center'\n"
            + "  },\n"
            + "  'text': {\n"
            + "    'data': 'Click Here',\n"
            + "    'size': 36,\n"
            + "    'style': 'bold',\n"
            + "    'name': 'text1',\n"
            + "    'hOffset': 250,\n"
            + "    'vOffset': 100,\n"
            + "    'alignment': 'center',\n"
            + "    'onMouseUp': 'sun1.opacity = (sun1.opacity / 100) * 90;'\n"
            + "  }\n"
            + "}}");

        xstream.alias("widget", Widget.class);
        xstream.alias("window", Window.class);
        xstream.alias("image", Image.class);
        xstream.alias("text", Text.class);

        Widget widget = new Widget();

        assertEquals(expected, xstream.toXML(widget));

    }

    public static class Widget {
        String debug = "on";
        Window window = new Window();
        Image image = new Image();
        Text text = new Text();
    }

    public static class Window {
        String title = "Sample Konfabulator Widget";
        String name = "main_window";
        int width = 500;
        int height = 500;
    }

    public static class Image {
        String src = "Images/Sun.png";
        String name = "sun1";
        int hOffset = 250;
        int vOffset = 250;
        String alignment = "center";
    }

    public static class Text {
        String data = "Click Here";
        int size = 36;
        String style = "bold";
        String name = "text1";
        int hOffset = 250;
        int vOffset = 100;
        String alignment = "center";
        String onMouseUp = "sun1.opacity = (sun1.opacity / 100) * 90;";
    }

    public void testColor() {
        boolean isHeadless = Boolean.valueOf(System.getProperty("java.awt.headless", "false")).booleanValue();
        if (!isHeadless || JVM.is15()) {
            Color color = Color.black;
            String expected = normalizeExpectation("" // 
                + "{'awt-color': {\n"
                + "  'red': 0,\n"
                + "  'green': 0,\n"
                + "  'blue': 0,\n"
                + "  'alpha': 255\n"
                + "}}");
            assertEquals(expected, xstream.toXML(color));
        }
    }

    public void testDoesHandleQuotesAndEscapes() {
        String[] strings = new String[]{
            "last\"", "\"first", "\"between\"", "around \"\" it", "back\\slash",};
        String expected = normalizeExpectation(""
            + "{'string-array': [\n"
            + "  'last\\\"',\n"
            + "  '\\\"first',\n"
            + "  '\\\"between\\\"',\n"
            + "  'around \\\"\\\" it',\n"
            + "  'back\\\\slash'\n"
            + "]}");
        assertEquals(expected, xstream.toXML(strings));
    }

    public void testDoesEscapeValuesAccordingRfc4627() {
        String expected = normalizeExpectation("{'string': '\\u0000\\u0001\\u001f \uffee'}");
        assertEquals(expected, xstream.toXML("\u0000\u0001\u001f\u0020\uffee"));
    }

    public void testSimpleInteger() {
        String expected = normalizeExpectation("{'int': 123}");
        assertEquals(expected, xstream.toXML(new Integer(123)));
    }

    public void testBracesAndSquareBracketsAreNotEscaped() {
        String expected = normalizeExpectation("{'string': '..{}[],,'}");
        assertEquals(expected, xstream.toXML("..{}[],,"));
    }

    public void testCanMarshalSimpleTypesWithNullMembers() {
        Msg message = new Msg("hello");
        Msg message2 = new Msg(null);
        message.innerMessage = message2;

        xstream.alias("innerMessage", Msg.class);

        String expected = normalizeExpectation(""
            + "{'innerMessage': {\n"
            + "  'greeting': 'hello',\n"
            + "  'innerMessage': {}\n"
            + "}}");
        assertEquals(expected, xstream.toXML(message));
    }

    public static class Msg {
        String greeting;
        Msg innerMessage;

        public Msg(String greeting) {
            this.greeting = greeting;
        }
    }

    public void testCanMarshalElementWithEmptyArray() {
        xstream.alias("element", ElementWithEmptyArray.class);

        String expected = normalizeExpectation("" // 
            + "{'element': {\n"
            + "  'array': []\n"
            + "}}");
        assertEquals(expected, xstream.toXML(new ElementWithEmptyArray()));
    }

    public static class ElementWithEmptyArray {
        String[] array = new String[0];
    }

    public void testCanMarshalJavaMap() {
        String entry1 = "" // entry 1
            + "  [\n"
            + "    'one',\n"
            + "    1\n"
            + "  ]";
        String entry2 = "" // entry 2
            + "  [\n"
            + "    'two',\n"
            + "    2\n"
            + "  ]";

        final Map map = new HashMap();
        map.put("one", new Integer(1));
        map.put("two", new Integer(2));
        String actual = xstream.toXML(map);
        int idx1 = actual.indexOf("one");
        int idx2 = actual.indexOf("two");

        String expected = normalizeExpectation(""
            + "{'map': [\n"
            + ((idx1 < idx2 ? entry1 : entry2) + ",\n")
            + ((idx1 < idx2 ? entry2 : entry1) + "\n") // no comma
        + "]}");
        assertEquals(expected, actual);
    }

    public void testCanMarshalProperties() {
        String entry1 = "" // entry 1
            + "  {\n"
            + "    '@name': 'one',\n"
            + "    '@value': '1'\n"
            + "  }";
        String entry2 = "" // entry 2
            + "  {\n"
            + "    '@name': 'two',\n"
            + "    '@value': '2'\n"
            + "  }";

        final Properties properties = new Properties();
        properties.setProperty("one", "1");
        properties.setProperty("two", "2");
        String actual = xstream.toXML(properties);
        int idx1 = actual.indexOf("one");
        int idx2 = actual.indexOf("two");

        String expected = normalizeExpectation(""
            + "{'properties': [\n"
            + ((idx1 < idx2 ? entry1 : entry2) + ",\n")
            + ((idx1 < idx2 ? entry2 : entry1) + "\n") // no comma
        + "]}");
        assertEquals(expected, actual);
    }

    final static class MapHolder {
        private Map map = new HashMap();
    }

    public void testCanMarshalNestedMap() {
        xstream.alias("holder", MapHolder.class);
        String entry1 = "" // entry 1
            + "    [\n"
            + "      'one',\n"
            + "      1\n"
            + "    ]";
        String entry2 = "" // entry 2
            + "    [\n"
            + "      'two',\n"
            + "      2\n"
            + "    ]";

        final MapHolder holder = new MapHolder();
        holder.map.put("one", new Integer(1));
        holder.map.put("two", new Integer(2));
        String actual = xstream.toXML(holder);
        int idx1 = actual.indexOf("one");
        int idx2 = actual.indexOf("two");

        String expected = normalizeExpectation(""
            + "{'holder': {\n"
            + "  'map': [\n"
            + ((idx1 < idx2 ? entry1 : entry2) + ",\n")
            + ((idx1 < idx2 ? entry2 : entry1) + "\n")
            + "  ]\n" // no comma
        + "}}");
        assertEquals(expected, actual);
    }

    static class CollectionKeeper {
        Collection coll = new ArrayList();
    }

    public void testIgnoresAttributeForCollectionMember() {
        xstream.alias("keeper", CollectionKeeper.class);
        String expected = normalizeExpectation("" //
            + "{'keeper': {\n"
            + "  'coll': [\n"
            + "    'one',\n"
            + "    'two'\n"
            + "  ]\n"
            + "}}");

        final CollectionKeeper holder = new CollectionKeeper();
        holder.coll.add("one");
        holder.coll.add("two");
        assertEquals(expected, xstream.toXML(holder));
    }

    // Writing attributes, the writer has no clue about their original type.
    public void testDoesWriteAttributesAsStringValues() {
        xstream.alias("window", Window.class);
        xstream.useAttributeFor("width", int.class);
        xstream.useAttributeFor("height", int.class);
        String expected = normalizeExpectation(""
            + "{'window': {\n"
            + "  '@width': '500',\n"
            + "  '@height': '500',\n"
            + "  'title': 'JUnit'\n"
            + "}}");

        final Window window = new Window();
        window.title = "JUnit";
        window.name = null;
        assertEquals(expected, xstream.toXML(window));
    }

    static class Person {
        String firstName;
        String lastName;
        Calendar dateOfBirth;
        Map titles = new TreeMap();
    }

    public void testCanWriteEmbeddedCalendar() {
        xstream.alias("person", Person.class);
        String expected = normalizeExpectation(""
            + "{'list': [\n"
            + "  {\n"
            + "    'firstName': 'Joe',\n"
            + "    'lastName': 'Walnes',\n"
            + "    'dateOfBirth': {\n"
            + "      'time': -2177539200000,\n"
            + "      'timezone': 'Europe/London'\n"
            + "    },\n"
            + "    'titles': [\n"
            + "      [\n"
            + "        '1',\n"
            + "        'Mr'\n"
            + "      ]\n"
            + "    ]\n"
            + "  }\n"
            + "]}");

        Person person = new Person();
        person.firstName = "Joe";
        person.lastName = "Walnes";
        person.dateOfBirth = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        person.dateOfBirth.clear();
        person.dateOfBirth.set(1900, Calendar.DECEMBER, 31);
        person.titles.put("1", "Mr");
        List list = new ArrayList();
        list.add(person);
        assertEquals(expected, xstream.toXML(list));
    }

    static class SingleValue {
        long l;
        URL url;
    }

    public void testSupportsAllConvertersWithASingleValue() throws MalformedURLException {
        xstream.alias("sv", SingleValue.class);
        String expected = normalizeExpectation(""
            + "{'sv': {\n"
            + "  'l': 4711,\n"
            + "  'url': 'http://localhost:8888'\n"
            + "}}");

        SingleValue value = new SingleValue();
        value.l = 4711;
        value.url = new URL("http://localhost:8888");
        assertEquals(expected, xstream.toXML(value));
    }
    
    static class SystemAttributes {
        String name;
        Object object;
        Original original;
    }
    
    public void testWillWriteTagValueAsDefaultValueIfNecessary() {
        xstream.alias("sa", SystemAttributes.class);
        xstream.alias("original", Original.class);
        xstream.alias("replaced", Replaced.class);

        SystemAttributes sa = new SystemAttributes();
        sa.name = "joe";
        sa.object = "walnes";
        sa.original = new Original("hello world");

        String expected = normalizeExpectation(""
                + "{'sa': {\n"
                + "  'name': 'joe',\n"
                + "  'object': {\n"
                + "    '@class': 'string',\n"
                + "    '$': 'walnes'\n" 
                +"  },\n"
                + "  'original': {\n"
                + "    '@resolves-to': 'replaced',\n"
                + "    'replacedValue': 'HELLO WORLD'\n"
                + "  }\n"
                + "}}");

        assertEquals(expected, xstream.toXML(sa));
    }

    public void testCanMarshalExternalizable() {
        xstream.alias("ext", SomethingExternalizable.class);
        
        SomethingExternalizable in = new SomethingExternalizable("Joe", "Walnes");
        String expected = normalizeExpectation(""
            + "{'ext': [\n"
            + "  3,\n"
            + "  'JoeWalnes',\n"
            + "  {},\n"
            + "  'XStream'\n"
            + "]}");

        assertEquals(expected, xstream.toXML(in));
    }

    public void testCanMarshalEmbeddedExternalizable() {
        xstream.alias("owner", OwnerOfExternalizable.class);
        
        OwnerOfExternalizable in = new OwnerOfExternalizable();
        in.target = new SomethingExternalizable("Joe", "Walnes");
        String expected = normalizeExpectation(""
            + "{'owner': {\n"
            + "  'target': [\n"
            + "    3,\n"
            + "    'JoeWalnes',\n"
            + "    {},\n"
            + "    'XStream'\n"
            + "  ]\n"
            + "}}");

        assertEquals(expected, xstream.toXML(in));
    }
}
