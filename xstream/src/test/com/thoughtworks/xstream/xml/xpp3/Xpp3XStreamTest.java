package com.thoughtworks.xstream.xml.xpp3;

import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.someobjects.FunnyConstructor;
import com.thoughtworks.someobjects.WithList;
import com.thoughtworks.someobjects.X;
import com.thoughtworks.someobjects.Y;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.alias.DefaultClassMapper;
import com.thoughtworks.xstream.alias.DefaultNameMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.objecttree.reflection.SunReflectionObjectFactory;
import junit.framework.TestCase;

public class Xpp3XStreamTest extends TestCase {
    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();

        xstream = new XStream(new SunReflectionObjectFactory(), new DefaultClassMapper(new DefaultNameMapper()),new Xpp3DomXMLReaderDriver());
        xstream.alias("x", X.class);
        xstream.alias("y", Y.class);
        xstream.alias("funny", FunnyConstructor.class);
        xstream.alias("with-list", WithList.class);
    }

    public void testUnmarshalsObjectFromXml() {

        String xml =
                "<x>" +
                "  <aStr>joe</aStr>" +
                "  <anInt>8</anInt>" +
                "  <innerObj>" +
                "    <yField>walnes</yField>" +
                "  </innerObj>" +
                "</x>";

        X x = (X) xstream.fromXML(xml);

        assertEquals("joe", x.aStr);
        assertEquals(8, x.anInt);
        assertEquals("walnes", x.innerObj.yField);
    }

    public void testMarshalsObjectToXml() {
        X x = new X();
        x.anInt = 9;
        x.aStr = "zzz";
        x.innerObj = new Y();
        x.innerObj.yField = "ooo";

        String expected =
                "<x>\n" +
                "  <aStr>zzz</aStr>\n" +
                "  <anInt>9</anInt>\n" +
                "  <innerObj>\n" +
                "    <yField>ooo</yField>\n" +
                "  </innerObj>\n" +
                "</x>";

        assertEquals(expected, xstream.toXML(x));
    }

    public void testUnmarshalsClassWithoutDefaultConstructor() {
        String xml =
                "<funny>" +
                "  <i>999</i>" +
                "</funny>";

        FunnyConstructor funnyConstructor = (FunnyConstructor) xstream.fromXML(xml);

        assertEquals(999, funnyConstructor.i);
    }

    public void testHandlesLists() {
        WithList original = new WithList();
        Y y = new Y();
        y.yField = "a";
        original.things.add(y);
        original.things.add(new FunnyConstructor(3));
        original.things.add(new FunnyConstructor(1));

        String xml = xstream.toXML(original);

        String expected =
                "<with-list>\n" +
                "  <things>\n" +
                "    <y>\n" +
                "      <yField>a</yField>\n" +
                "    </y>\n" +
                "    <funny>\n" +
                "      <i>3</i>\n" +
                "    </funny>\n" +
                "    <funny>\n" +
                "      <i>1</i>\n" +
                "    </funny>\n" +
                "  </things>\n" +
                "</with-list>";

        assertEquals(expected, xml);

        WithList result = (WithList) xstream.fromXML(xml);
        assertEquals(original, result);

    }

    public void testNonStaticPrivateInnerClassCanBeUsed() {
        NonStaticInnerClass obj = new NonStaticInnerClass();
        obj.field = 3;

        xstream.alias("inner", NonStaticInnerClass.class);

        String xml = xstream.toXML(obj);

        String expected =
                "<inner>\n" +
                "  <field>3</field>\n" +
                "</inner>";

        assertEquals(expected, xml);

        NonStaticInnerClass result = (NonStaticInnerClass) xstream.fromXML(xml);
        assertEquals(obj.field, result.field);
    }

    public void testClassWithoutMappingUsesFullyQualifiedName() {
        NonStaticInnerClass obj = new NonStaticInnerClass();
        obj.field = 3;

        String xml = xstream.toXML(obj);

        String expected =
                "<com.thoughtworks.xstream.xml.xpp3.Xpp3XStreamTest-NonStaticInnerClass>\n" +
                "  <field>3</field>\n" +
                "</com.thoughtworks.xstream.xml.xpp3.Xpp3XStreamTest-NonStaticInnerClass>";

        assertEquals(expected, xml);

        NonStaticInnerClass result = (NonStaticInnerClass) xstream.fromXML(xml);

        assertEquals(obj, result);
    }

    private class NonStaticInnerClass extends StandardObject {
        int field;
    }

    public void testObjectsCanBeConvertedMultipleTimesWithSameXStream() {
        Y obj = new Y();
        obj.yField = "x";

        assertEquals(xstream.toXML(obj), xstream.toXML(obj));
    }

    public void testXStreamWithPeekMethodWithUnderlyingXpp3Implementation()
            throws Exception {

        String xml =
                "<person>" +
                "  <firstName>jason</firstName>" +
                "  <lastName>van Zyl</lastName>" +
                "  <element>" +
                "    <foo>bar</foo>" +
                "  </element>" +
                "</person>";

        xstream.registerConverter(new ElementConverter());

        xstream.alias("person", Person.class);

        Xpp3DomXMLReaderDriver driver = new Xpp3DomXMLReaderDriver();

        Person person = (Person) xstream.fromXML(driver.createReader(xml));

        assertEquals("jason", person.firstName);

        assertEquals("van Zyl", person.lastName);

        assertNotNull(person.element);

        assertEquals("bar", person.element.getChild("foo").getValue());
    }

    static class Person {
        String firstName;
        String lastName;
        Xpp3Dom element;
    }

    private class ElementConverter implements Converter {
        public boolean canConvert(Class type) {
            return Xpp3Dom.class.isAssignableFrom(type);
        }

        public void toXML(MarshallingContext context) {
        }

        public Object fromXML(UnmarshallingContext context) {
            Xpp3Dom element = (Xpp3Dom) context.xmlPeek();

            while (context.xmlNextChild()) {
                context.xmlPop();
            }

            return element;
        }
    }

    public void testXStreamPopulatingAnObjectGraphStartingWithALiveRootObject()
            throws Exception {

        String xml =
                "<component>" +
                "  <host>host</host>" +
                "  <port>8000</port>" +
                "</component>";

        xstream.alias("component", Component.class);

        Xpp3DomXMLReaderDriver driver = new Xpp3DomXMLReaderDriver();

        Component component0 = new Component();

        Component component1 = (Component) xstream.fromXML(driver.createReader(xml), component0);

        assertSame(component0, component1);

        assertEquals("host", component0.host);

        assertEquals(8000, component0.port);
    }

    static class Component {
        String host;
        int port;
    }

}
