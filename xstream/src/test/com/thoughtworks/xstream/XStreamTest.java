/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2014, 2017, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.dom4j.Element;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.FunnyConstructor;
import com.thoughtworks.acceptance.someobjects.Handler;
import com.thoughtworks.acceptance.someobjects.HandlerManager;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.U;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.AbstractDocumentReader;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

import junit.framework.TestCase;


public class XStreamTest extends TestCase {

    private transient XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("x", X.class);
        xstream.alias("y", Y.class);
        xstream.alias("funny", FunnyConstructor.class);
        xstream.alias("with-list", WithList.class);
    }

    public void testUnmarshalsObjectFromXmlWithUnderscores() {
        final String xml = ""//
            + "<u-u>"
            + "  <u-f>foo</u-f>"
            + "  <u_f>_foo</u_f>"
            + "</u-u>";

        xstream.alias("u-u", U.class);
        xstream.aliasField("u-f", U.class, "aStr");
        xstream.aliasField("u_f", U.class, "a_Str");
        final U u = (U)xstream.fromXML(xml);

        assertEquals("foo", u.aStr);
        assertEquals("_foo", u.a_Str);
    }

    public void testUnmarshalsObjectFromXmlWithClassContainingUnderscores() {
        final String xml = ""//
            + "<com.thoughtworks.xstream.XStreamTest_-U_U>"
            + "  <aStr>custom value</aStr>"
            + "</com.thoughtworks.xstream.XStreamTest_-U_U>";

        final U_U u = (U_U)xstream.fromXML(xml);

        assertEquals("custom value", u.aStr);
    }

    public void testUnmarshalsObjectFromXmlWithUnderscoresWithoutAliasingFields() {
        final String xml = ""//
            + "<u-u>"
            + "  <a_Str>custom value</a_Str>"
            + "</u-u>";

        xstream.alias("u-u", U.class);

        final U u = (U)xstream.fromXML(xml);

        assertEquals("custom value", u.a_Str);
    }

    public static class U_U {
        String aStr;
    }

    public void testUnmarshalsObjectFromXml() {

        final String xml = ""//
            + "<x>"
            + "  <aStr>joe</aStr>"
            + "  <anInt>8</anInt>"
            + "  <innerObj>"
            + "    <yField>walnes</yField>"
            + "  </innerObj>"
            + "</x>";

        final X x = (X)xstream.fromXML(xml);

        assertEquals("joe", x.aStr);
        assertEquals(8, x.anInt);
        assertEquals("walnes", x.innerObj.yField);
    }

    public void testMarshalsObjectToXml() {
        final X x = new X();
        x.anInt = 9;
        x.aStr = "zzz";
        x.innerObj = new Y();
        x.innerObj.yField = "ooo";

        final String expected = ""//
            + "<x>\n"
            + "  <aStr>zzz</aStr>\n"
            + "  <anInt>9</anInt>\n"
            + "  <innerObj>\n"
            + "    <yField>ooo</yField>\n"
            + "  </innerObj>\n"
            + "</x>";

        assertEquals(xstream.fromXML(expected), x);
    }

    public void testUnmarshalsClassWithoutDefaultConstructor() {
        final String xml = ""//
            + "<funny>"
            + "  <i>999</i>"
            + "</funny>";

        final FunnyConstructor funnyConstructor = (FunnyConstructor)xstream.fromXML(xml);

        assertEquals(999, funnyConstructor.i);
    }

    public void testHandlesLists() {
        final WithList<Object> original = new WithList<>();
        final Y y = new Y();
        y.yField = "a";
        original.things.add(y);
        original.things.add(new X(3));
        original.things.add(new X(1));

        final String xml = xstream.toXML(original);

        final String expected = ""//
            + "<with-list>\n"
            + "  <things>\n"
            + "    <y>\n"
            + "      <yField>a</yField>\n"
            + "    </y>\n"
            + "    <x>\n"
            + "      <anInt>3</anInt>\n"
            + "    </x>\n"
            + "    <x>\n"
            + "      <anInt>1</anInt>\n"
            + "    </x>\n"
            + "  </things>\n"
            + "</with-list>";

        assertEquals(expected, xml);

        final WithList<Object> result = xstream.fromXML(xml);
        assertEquals(original, result);

    }

    public void testCanHandleNonStaticPrivateInnerClass() {
        final NonStaticInnerClass obj = new NonStaticInnerClass();
        obj.field = 3;

        xstream.alias("inner", NonStaticInnerClass.class);

        final String xml = xstream.toXML(obj);

        final String expected = ""
            + "<inner>\n"
            + "  <field>3</field>\n"
            + "  <outer-class>\n"
            + "    <fName>testCanHandleNonStaticPrivateInnerClass</fName>\n"
            + "  </outer-class>\n"
            + "</inner>";

        assertEquals(xstream.fromXML(expected), obj);

        final NonStaticInnerClass result = (NonStaticInnerClass)xstream.fromXML(xml);
        assertEquals(obj.field, result.field);
    }

    public void testClassWithoutMappingUsesFullyQualifiedName() {
        final Person obj = new Person();

        final String xml = xstream.toXML(obj);

        final String expected = "<com.thoughtworks.xstream.XStreamTest_-Person/>";

        assertEquals(expected, xml);

        final Person result = (Person)xstream.fromXML(xml);
        assertEquals(obj, result);
    }

    private class NonStaticInnerClass extends StandardObject {
        private static final long serialVersionUID = 200310L;
        int field;
    }

    public void testCanBeBeUsedMultipleTimesWithSameInstance() {
        final Y obj = new Y();
        obj.yField = "x";

        assertEquals(xstream.toXML(obj), xstream.toXML(obj));
    }

    public void testAccessToUnderlyingDom4JImplementation() throws Exception {

        final String xml = ""//
            + "<person>"
            + "  <firstName>jason</firstName>"
            + "  <lastName>van Zyl</lastName>"
            + "  <element>"
            + "    <foo>bar</foo>"
            + "  </element>"
            + "</person>";

        xstream.registerConverter(new ElementConverter());
        xstream.alias("person", Person.class);

        final Dom4JDriver driver = new Dom4JDriver();
        @SuppressWarnings("resource")
        final Person person = (Person)xstream.unmarshal(driver.createReader(new StringReader(xml)));

        assertEquals("jason", person.firstName);
        assertEquals("van Zyl", person.lastName);
        assertNotNull(person.element);
        assertEquals("bar", person.element.element("foo").getText());
    }

    public static class Person extends StandardObject {
        private static final long serialVersionUID = 200405L;
        String firstName;
        String lastName;
        Element element;
    }

    private class ElementConverter implements Converter {

        @Override
        public boolean canConvert(final Class<?> type) {
            return Element.class.isAssignableFrom(type);
        }

        @Override
        public void marshal(final Object source, final HierarchicalStreamWriter writer,
                final MarshallingContext context) {
        }

        @SuppressWarnings("resource")
        @Override
        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {

            final AbstractDocumentReader documentReader = (AbstractDocumentReader)reader.underlyingReader();
            final Element element = (Element)documentReader.getCurrent();

            while (reader.hasMoreChildren()) {
                reader.moveDown();
                reader.moveUp();
            }

            return element;
        }
    }

    public void testPopulationOfAnObjectGraphStartingWithALiveRootObject() throws Exception {

        final String xml = ""//
            + "<component>"
            + "  <host>host</host>"
            + "  <port>8000</port>"
            + "</component>";

        xstream.alias("component", Component.class);

        final Component component0 = new Component();
        final Component component1 = xstream.fromXML(xml, component0);
        assertSame(component0, component1);
        assertEquals("host", component0.host);
        assertEquals(8000, component0.port);
    }

    static class Component {
        String host;
        int port;
    }

    public void testPopulationOfThisAsRootObject() throws Exception {

        final String xml = ""//
            + "<component>\n"
            + "  <host>host</host>\n"
            + "  <port>8000</port>\n"
            + "</component>";

        xstream.alias("component", SelfSerializingComponent.class);
        final SelfSerializingComponent component = new SelfSerializingComponent();
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
        String toXML(final XStream xstream) {
            return xstream.toXML(this);
        }

        void fromXML(final XStream xstream, final String xml) {
            xstream.fromXML(xml, this);
        }
    }

    public void testUnmarshalsWhenAllImplementationsAreSpecifiedUsingAClassIdentifier() throws Exception {

        final String xml = ""//
            + "<handlerManager class='com.thoughtworks.acceptance.someobjects.HandlerManager'>"
            + "  <handlers>"
            + "    <handler class='com.thoughtworks.acceptance.someobjects.Handler'>"
            + "      <protocol class='com.thoughtworks.acceptance.someobjects.Protocol'>"
            + "        <id>foo</id> "
            + "      </protocol>  "
            + "    </handler>"
            + "  </handlers>"
            + "</handlerManager>";

        final HandlerManager hm = (HandlerManager)xstream.fromXML(xml);
        final Handler h = hm.getHandlers().get(0);
        final Protocol p = h.getProtocol();
        assertEquals("foo", p.getId());
    }

    public void testObjectOutputStreamCloseTwice() throws IOException {
        final ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(Integer.valueOf(1));
        oout.close();
        oout.close();
    }

    public void testObjectOutputStreamCloseAndFlush() throws IOException {
        final ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(Integer.valueOf(1));
        oout.close();
        try {
            oout.flush();
            fail("Closing and flushing should throw a StreamException");
        } catch (final StreamException e) {
            // ok
        }
    }

    public void testObjectOutputStreamCloseAndWrite() throws IOException {
        final ObjectOutputStream oout = xstream.createObjectOutputStream(new StringWriter());
        oout.writeObject(Integer.valueOf(1));
        oout.close();
        try {
            oout.writeObject(Integer.valueOf(2));
            fail("Closing and writing should throw a StreamException");
        } catch (final StreamException e) {
            // ok
        }
    }

    public void testUnmarshalsFromFile() throws IOException {
        final File file = createTestFile();
        xstream.registerConverter(new ElementConverter());
        xstream.alias("component", Component.class);
        final Component person = (Component)xstream.fromXML(file);
        assertEquals(8000, person.port);
    }

    public void testUnmarshalsFromURL() throws IOException {
        final File file = createTestFile();
        xstream.alias("component", Component.class);
        final Component person = (Component)xstream.fromXML(file);
        assertEquals(8000, person.port);
    }

    private File createTestFile() throws FileNotFoundException, IOException, UnsupportedEncodingException {
        final String xml = "" //
            + "<component>\n"
            + "  <host>host</host>\n"
            + "  <port>8000</port>\n"
            + "</component>";

        final File dir = new File("target/test-data");
        dir.mkdirs();
        final File file = new File(dir, "test.xml");
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(xml.getBytes("UTF-8"));
        }
        return file;
    }
}
