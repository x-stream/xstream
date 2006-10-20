package com.thoughtworks.xstream.io.json;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.awt.Color;
import java.io.Reader;
import java.io.InputStream;

/**
 * Some of these test cases are taken from example JSON listed at http://www.json.org/example.html
 *
 * @author Paul Hammant
 */
public class JsonHierarchicalStreamDriverTest extends TestCase {

    public void testReadingFromReaderNotSupported() {
        try {
            new JsonHierarchicalStreamDriver().createReader((Reader) null);
            fail("should have barfed");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    public void testReadingFromInputStreamNotSupported() {
        try {
            new JsonHierarchicalStreamDriver().createReader((InputStream) null);
            fail("should have barfed");
        } catch (UnsupportedOperationException uoe) {
            // expected
        }
    }

    public void testSimpleTypesCanBeRepresentedAsJson() {

        String expected = (
                "{'innerMessage': {\n" +
                        "  'greeting': 'hello',\n" +
                        "  'num1': 2,\n" +
                        "  'num2': 3,\n" +
                        "  'bool': true,\n" +
                        "  'bool2': true,\n" +
                        "  'innerMessage': {\n" +
                        "    'greeting': 'bonjour',\n" +
                        "    'num1': 3,\n" +
                        "    'bool': false\n" +
                        "  }\n" +
                        "}}").replace('\'', '"');

        XStream xs = new XStream(new JsonHierarchicalStreamDriver());

        xs.alias("innerMessage", Message.class);

        Message message = new Message("hello");
        message.num1 = 2;
        message.num2 = new Integer(3);
        message.bool = true;
        message.bool2 = Boolean.TRUE;

        Message message2 = new Message("bonjour");
        message2.num1 = 3;

        message.innerMessage = message2;

        assertEquals(expected, xs.toXML(message));
    }

    public static class Message {
        String greeting;
        int num1;
        Integer num2;
        boolean bool;
        Boolean bool2;
        Message innerMessage;

        public Message(String greeting) {
            this.greeting = greeting;
        }
    }

    String expected = (
            "{'menu': {\n" +
                    "  'id': 'file',\n" +
                    "  'value': 'File:',\n" +
                    "  'popup': {\n" +
                    "    'menuitem': [\n" +
                    "      {\n" +
                    "        'value': 'New',\n" +
                    "        'onclick': 'CreateNewDoc()'\n" +
                    "      },\n" +
                    "      {\n" +
                    "        'value': 'Open',\n" +
                    "        'onclick': 'OpenDoc()'\n" +
                    "      },\n" +
                    "      {\n" +
                    "        'value': 'Close',\n" +
                    "        'onclick': 'CloseDoc()'\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}}").replace('\'', '"');

    public void testListsRepresentedCorrectlyAsJson() {

        // This from http://www.json.org/example.html

        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        xs.alias("menu", MenuWithList.class);
        xs.alias("menuitem", MenuItem.class);

        MenuWithList menu = new MenuWithList();

        assertEquals(expected, xs.toXML(menu));
    }

    public void testArraysRepresentedCorrectlyAsJson() {


        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        xs.alias("menu", MenuWithArray.class);
        xs.alias("menuitem", MenuItem.class);

        MenuWithArray menu = new MenuWithArray();

        assertEquals(expected, xs.toXML(menu));
    }

    public void testSetsRepresentedCorrectlyAsJson() {

        // This from http://www.json.org/example.html

        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        xs.alias("menu", MenuWithSet.class);
        xs.alias("menuitem", MenuItem.class);

        MenuWithSet menu = new MenuWithSet();

        assertEquals(expected, xs.toXML(menu));
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
                new MenuItem("New", "CreateNewDoc()"),
                new MenuItem("Open", "OpenDoc()"),
                new MenuItem("Close", "CloseDoc()")
        };
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


    public void testRepresentationOfTypeWithPrimitviesAsJson() {

        // This also from http://www.expected.org/example.html

        String expected = (
                "{'widget': {\n" +
                        "  'debug': 'on',\n" +
                        "  'window': {\n" +
                        "    'title': 'Sample Konfabulator Widget',\n" +
                        "    'name': 'main_window',\n" +
                        "    'width': 500,\n" +
                        "    'height': 500\n" +
                        "  },\n" +
                        "  'image': {\n" +
                        "    'src': 'Images/Sun.png',\n" +
                        "    'name': 'sun1',\n" +
                        "    'hOffset': 250,\n" +
                        "    'vOffset': 250,\n" +
                        "    'alignment': 'center'\n" +
                        "  },\n" +
                        "  'text': {\n" +
                        "    'data': 'Click Here',\n" +
                        "    'size': 36,\n" +
                        "    'style': 'bold',\n" +
                        "    'name': 'text1',\n" +
                        "    'hOffset': 250,\n" +
                        "    'vOffset': 100,\n" +
                        "    'alignment': 'center',\n" +
                        "    'onMouseUp': 'sun1.opacity = (sun1.opacity / 100) * 90;'\n" +
                        "  }\n" +
                        "}}").replace('\'', '"');

        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        xs.alias("widget", Widget.class);
        xs.alias("window", Window.class);
        xs.alias("image", Image.class);
        xs.alias("text", Text.class);

        Widget widget = new Widget();

        assertEquals(expected, xs.toXML(widget));

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
        Color color = Color.black;
        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        String expected = (
                "{'awt-color': {\n"
            + "  'red': {'0'},\n"
            + "  'green': {'0'},\n"
            + "  'blue': {'0'},\n"
            + "  'alpha': {'255'}\n"
            + "}}").replace('\'', '"');
        assertEquals(expected, xs.toXML(color));
    }
    
    public void testQuoteHandlingAndEscapes() {
        String[] strings = new String[] {
           "last\"" ,
            "\"first" ,
            "\"between\"" ,
            "around \"\" it" ,
            "back\\slash" ,
        };
        XStream xs = new XStream(new JsonHierarchicalStreamDriver());
        String expected = (""
            + "{#string-array#: [\n"
            + "  #last\\\"#,\n"
            + "  #\\\"first#,\n"
            + "  #\\\"between\\\"#,\n"
            + "  #around \\\"\\\" it#,\n"
            + "  #back\\\\slash#\n"
            + "]}").replace('#', '"');
        assertEquals(expected, xs.toXML(strings));
    }
}

