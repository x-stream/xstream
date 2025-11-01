/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.Employee;
import com.thoughtworks.acceptance.someobjects.FunnyConstructor;
import com.thoughtworks.acceptance.someobjects.Handler;
import com.thoughtworks.acceptance.someobjects.HandlerManager;
import com.thoughtworks.acceptance.someobjects.PhoneNumber;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.TestPerson;
import com.thoughtworks.acceptance.someobjects.U;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.AbstractDocumentReader;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.security.NoTypePermission;

import junit.framework.TestCase;

import org.dom4j.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class XStreamTest extends TestCase {

    private transient XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.addPermission(NoTypePermission.NONE); // clear out defaults
        xstream.allowTypesByWildcard(new String[]{
            AbstractAcceptanceTest.class.getPackage().getName()+".*objects.**",
            this.getClass().getName()+"$*"
        });
        xstream.alias("x", X.class);
        xstream.alias("y", Y.class);
        xstream.alias("funny", FunnyConstructor.class);
        xstream.alias("with-list", WithList.class);
    }

    public void testUnmarshalObjectsWithDefaultLimits() {
        String xml = "<P1>\n" +
                "  <firstname>John</firstname>\n" +
                "  <lastname>Doe</lastname>\n" +
                "  <phone>\n" +
                "    <code>123</code>\n" +
                "    <number>6789</number>\n" +
                "  </phone>\n" +
                "</P1>";

        XStream xstreamLimit = new XStream();
        xstreamLimit.allowTypes(new Class[]{TestPerson.class, PhoneNumber.class});
        xstreamLimit.alias("P1", TestPerson.class);

        try {
            Object retrievedObject = xstreamLimit.fromXML(xml);
            assertEquals(TestPerson.class, retrievedObject.getClass());
        } catch (Exception e) {

            fail("Expected XStream to unmarshall");
        }
    }

    public void testUnmarshalObjectsWithDepthLimits() {
        String xml = "<P1>\n" +
                "  <firstname>John</firstname>\n" +
                "  <lastname>Doe</lastname>\n" +
                "  <phone>\n" +
                "    <code>123</code>\n" +
                "    <number>6789</number>\n" +
                "  </phone>\n" +
                "</P1>";

        XStream xstreamLimit = new XStream();
        xstreamLimit.allowTypes(new Class[]{TestPerson.class, PhoneNumber.class});
        xstreamLimit.alias("P1", TestPerson.class);

        xstreamLimit.setMaxAllowedLimits(1, 5, 5);
        try {
            xstreamLimit.fromXML(xml);
            fail("Expected XStreamException to be thrown");
        } catch (Exception e) {
            assertEquals(XStreamException.class, e.getCause().getClass());
            assertEquals("XML depth exceeds maximum allowed depth of 1", e.getCause().getMessage());
        }
    }

    public void testUnmarshalObjectsWithFieldsLimits() {
        String xml = "<P1>\n" +
                "  <firstname>John</firstname>\n" +
                "  <lastname>Doe</lastname>\n" +
                "  <phone>\n" +
                "    <code>123</code>\n" +
                "    <number>6789</number>\n" +
                "  </phone>\n" +
                "</P1>";

        XStream xstreamLimit = new XStream();
        xstreamLimit.allowTypes(new Class[]{TestPerson.class, PhoneNumber.class});
        xstreamLimit.alias("P1", TestPerson.class);

        xstreamLimit.setMaxAllowedLimits(5, 2, 5);

        try {
            xstreamLimit.fromXML(xml);
            fail("Expected XStreamException to be thrown");
        } catch (Exception e) {
            assertEquals(XStreamException.class, e.getCause().getClass());
            assertEquals("Encountered more fields than the maximum allowed size of 2", e.getCause().getMessage());
        }
    }

    public void testUnmarshalObjectsWithFieldsLimitsFromSuperClass() {
        String xml = "<com.thoughtworks.acceptance.someobjects.Employee>\n" +
                "  <firstname>John</firstname>\n" +
                "  <lastname>Doe</lastname>\n" +
                "  <phone>\n" +
                "    <code>123</code>\n" +
                "    <number>6789</number>\n" +
                "  </phone>\n" +
                "  <employeeId>E123</employeeId>\n" +
                "  <department>gPQR</department>\n" +
                "</com.thoughtworks.acceptance.someobjects.Employee>";

        XStream xstreamLimit = new XStream();
        xstreamLimit.allowTypes(new Class[]{TestPerson.class, PhoneNumber.class, Employee.class});
        xstreamLimit.setMaxAllowedLimits(5, 4, 5);

        try {
            xstreamLimit.fromXML(xml);
            fail("Expected XStreamException to be thrown");
        } catch (Exception e) {
            assertEquals(XStreamException.class, e.getCause().getClass());
            assertEquals("Encountered more fields than the maximum allowed size of 4", e.getCause().getMessage());
        }
    }

    public void testUnmarshalObjectsWithValueLimits() {
        String xml = "<P1>\n" +
                "  <firstname>John</firstname>\n" +
                "  <lastname>Doe</lastname>\n" +
                "  <phone>\n" +
                "    <code>123</code>\n" +
                "    <number>67890</number>\n" +
                "  </phone>\n" +
                "</P1>";

        XStream xstreamLimit = new XStream();
        xstreamLimit.allowTypes(new Class[]{TestPerson.class, PhoneNumber.class});
        xstreamLimit.alias("P1", TestPerson.class);

        xstreamLimit.setMaxAllowedLimits(5, 5, 4);

        try {
            xstreamLimit.fromXML(xml);
            fail("Expected XStreamException to be thrown");
        } catch (Exception e) {
            assertEquals(XStreamException.class, e.getCause().getClass());
            assertEquals("Size of value longer than the maximum allowed size of 4", e.getCause().getMessage());
        }
    }

    public void testUnmarshalsObjectFromXmlWithUnderscores() {
        String xml =
                "<u-u>" +
                "  <u-f>foo</u-f>" +
                "  <u_f>_foo</u_f>" +
                "</u-u>";

        xstream.alias("u-u", U.class);
        xstream.aliasField("u-f", U.class, "aStr");
        xstream.aliasField("u_f", U.class, "a_Str");
        U u = (U) xstream.fromXML(xml);

        assertEquals("foo", u.aStr);
        assertEquals("_foo", u.a_Str);
    }

    public void testUnmarshalsObjectFromXmlWithClassContainingUnderscores() {
        String xml =
                "<com.thoughtworks.xstream.XStreamTest_-U_U>" +
                "  <aStr>custom value</aStr>" +
                "</com.thoughtworks.xstream.XStreamTest_-U_U>";

        U_U u = (U_U) xstream.fromXML(xml);

        assertEquals("custom value", u.aStr);
    }


    public void testUnmarshalsObjectFromXmlWithUnderscoresWithoutAliasingFields() {
        String xml =
                "<u-u>" +
                "  <a_Str>custom value</a_Str>" +
                "</u-u>";

        xstream.alias("u-u", U.class);

        U u = (U) xstream.fromXML(xml);

        assertEquals("custom value", u.a_Str);
    }

    public static class U_U {
    	String aStr;
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

        assertEquals(xstream.fromXML(expected), x);
    }

    public void testUnmarshalsClassWithoutDefaultConstructor() {
        if (!JVM.is14()) return;

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
        original.things.add(new X(3));
        original.things.add(new X(1));

        String xml = xstream.toXML(original);

        String expected =
                "<with-list>\n" +
                "  <things>\n" +
                "    <y>\n" +
                "      <yField>a</yField>\n" +
                "    </y>\n" +
                "    <x>\n" +
                "      <anInt>3</anInt>\n" +
                "    </x>\n" +
                "    <x>\n" +
                "      <anInt>1</anInt>\n" +
                "    </x>\n" +
                "  </things>\n" +
                "</with-list>";

        assertEquals(expected, xml);

        WithList result = (WithList) xstream.fromXML(xml);
        assertEquals(original, result);

    }

    public void testCanHandleNonStaticPrivateInnerClass() {
        if (!JVM.is14()) return;

        NonStaticInnerClass obj = new NonStaticInnerClass();
        obj.field = 3;

        xstream.alias("inner", NonStaticInnerClass.class);

        String xml = xstream.toXML(obj);

        String expected = ""
                + "<inner>\n"
                + "  <field>3</field>\n"
                + "  <outer-class>\n"
                + "    <fName>testCanHandleNonStaticPrivateInnerClass</fName>\n"
                + "  </outer-class>\n"
                + "</inner>";

        assertEquals(xstream.fromXML(expected), obj);

        NonStaticInnerClass result = (NonStaticInnerClass) xstream.fromXML(xml);
        assertEquals(obj.field, result.field);
    }

    public void testClassWithoutMappingUsesFullyQualifiedName() {
        Person obj = new Person();

        String xml = xstream.toXML(obj);

        String expected = "<com.thoughtworks.xstream.XStreamTest_-Person/>";

        assertEquals(expected, xml);

        Person result = (Person) xstream.fromXML(xml);
        assertEquals(obj, result);
    }

    private class NonStaticInnerClass extends StandardObject {
        int field;
    }

    public void testCanBeBeUsedMultipleTimesWithSameInstance() {
        Y obj = new Y();
        obj.yField = "x";

        assertEquals(xstream.toXML(obj), xstream.toXML(obj));
    }

    public void testAccessToUnderlyingDom4JImplementation()
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

        Dom4JDriver driver = new Dom4JDriver();
        Person person = (Person) xstream.unmarshal(driver.createReader(new StringReader(xml)));

        assertEquals("jason", person.firstName);
        assertEquals("van Zyl", person.lastName);
        assertNotNull(person.element);
        assertEquals("bar", person.element.element("foo").getText());
    }

    public static class Person extends StandardObject {
        String firstName;
        String lastName;
        Element element;
    }

    private class ElementConverter implements Converter {

        public boolean canConvert(Class type) {
            return Element.class.isAssignableFrom(type);
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

            AbstractDocumentReader documentReader = (AbstractDocumentReader)reader.underlyingReader();
            Element element = (Element) documentReader.getCurrent();

            while (reader.hasMoreChildren()) {
                reader.moveDown();
                reader.moveUp();
            }

            return element;
        }
    }

    public void testPopulationOfAnObjectGraphStartingWithALiveRootObject()
            throws Exception {

        String xml =
                "<component>" +
                "  <host>host</host>" +
                "  <port>8000</port>" +
                "</component>";

        xstream.alias("component", Component.class);

        Component component0 = new Component();
        Component component1 = (Component) xstream.fromXML(xml, component0);
        assertSame(component0, component1);
        assertEquals("host", component0.host);
        assertEquals(8000, component0.port);
    }

    static class Component {
        String host;
        int port;
    }

    public void testPopulationOfThisAsRootObject()
            throws Exception {

        String xml =""
                + "<component>\n"
                + "  <host>host</host>\n"
                + "  <port>8000</port>\n"
                + "</component>";

        xstream.alias("component", SelfSerializingComponent.class);
        SelfSerializingComponent component = new SelfSerializingComponent();
        component.host = "host";
        component.port = 8000;
        assertEquals(xml, component.toXML(xstream));
        component.host = "foo";
        component.port = -1;
        component.fromXML(xstream, xml);
        assertEquals("host", component.host);
        assertEquals(8000, component.port);
    }

    static class SelfSerializingComponent extends Component {
        String toXML(XStream xstream) {
            return xstream.toXML(this);
        }
        void fromXML(XStream xstream, String xml) {
            xstream.fromXML(xml, this);
        }
    }

    public void testUnmarshalsWhenAllImplementationsAreSpecifiedUsingAClassIdentifier()
            throws Exception {

        String xml =
                "<handlerManager class='com.thoughtworks.acceptance.someobjects.HandlerManager'>" +
                "  <handlers>" +
                "    <handler class='com.thoughtworks.acceptance.someobjects.Handler'>" +
                "      <protocol class='com.thoughtworks.acceptance.someobjects.Protocol'>" +
                "        <id>foo</id> " +
                "      </protocol>  " +
                "    </handler>" +
                "  </handlers>" +
                "</handlerManager>";

        HandlerManager hm = (HandlerManager) xstream.fromXML(xml);
        Handler h = (Handler) hm.getHandlers().get(0);
        Protocol p = h.getProtocol();
        assertEquals("foo", p.getId());
    }

    public void testObjectOutputStreamCloseTwice() throws IOException {
        ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(new Integer(1));
        oout.close();
        oout.close();
    }

    public void testObjectOutputStreamCloseAndFlush() throws IOException {
        ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(new Integer(1));
        oout.close();
        try {
            oout.flush();
            fail("Closing and flushing should throw a StreamException");
        } catch (StreamException e) {
            // ok
        }
    }

    public void testObjectOutputStreamCloseAndWrite() throws IOException {
        ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(new Integer(1));
        oout.close();
        try {
            oout.writeObject(new Integer(2));
            fail("Closing and writing should throw a StreamException");
        } catch (StreamException e) {
            // ok
        }
    }

    public void testUnmarshalsFromFile() throws IOException {
        File file = createTestFile();
        xstream.registerConverter(new ElementConverter());
        xstream.alias("component", Component.class);
        Component person = (Component)xstream.fromXML(file);
        assertEquals(8000, person.port);
    }

    public void testUnmarshalsFromURL() throws IOException {
        File file = createTestFile();
        xstream.alias("component", Component.class);
        Component person = (Component)xstream.fromXML(file);
        assertEquals(8000, person.port);
    }

    private File createTestFile()
        throws FileNotFoundException, IOException, UnsupportedEncodingException {
        String xml =""
                + "<component>\n"
                + "  <host>host</host>\n"
                + "  <port>8000</port>\n"
                + "</component>";

        File dir = new File("target/test-data");
        dir.mkdirs();
        File file = new File(dir, "test.xml");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(xml.getBytes("UTF-8"));
        fos.close();
        return file;
    }
}
