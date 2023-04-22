/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014, 2018, 2019, 2020, 2023 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. March 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaFieldConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class AliasTest extends AbstractAcceptanceTest {

    public void testBarfsIfItDoesNotExist() {

        final String xml = "" //
            + "<X-array>\n"
            + "  <X>\n"
            + "    <anInt>0</anInt>\n"
            + "  </X>\n"
            + "</X-array>";

        // now change the alias
        xstream.alias("Xxxxxxxx", X.class);
        try {
            xstream.fromXML(xml);
            fail("ShouldCannotResolveClassException expected");
        } catch (final CannotResolveClassException expectedException) {
            // expected
        }
    }

    public void testWithUnderscore() {
        xstream = new XStream(DefaultDriver.create(new XmlFriendlyNameCoder("_-", "_")));
        setupSecurity(xstream);
        final String xml = ""//
            + "<X_alias>\n"
            + "  <anInt>0</anInt>\n"
            + "</X_alias>";

        // now change the alias
        xstream.alias("X_alias", X.class);
        final X x = new X(0);
        assertBothWays(x, xml);
    }

    public static class HasUnderscore {
        String _attr = "foo";
    }

    public void testWithPrefixedUnderscore() {
        final HasUnderscore x = new HasUnderscore();

        xstream.alias("underscore", HasUnderscore.class);
        xstream.aliasField("attr", HasUnderscore.class, "_attr");

        final String xml = "" //
            + "<underscore>\n"
            + "  <attr>foo</attr>\n"
            + "</underscore>";

        assertBothWays(x, xml);
    }

    public void testForFieldAsAttribute() {
        final Software software = new Software("walness", "xstream");

        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("id", "name");

        final String xml = "<software vendor=\"walness\" id=\"xstream\"/>";

        assertBothWays(software, xml);
    }

    public void testOmitAliasesAttributeWithRealName() {
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("id", "name");
        xstream.aliasAttribute("name", "foo");
        xstream.omitField(Software.class, "foo");

        final String xml = "<software vendor=\"walness\" name=\"xstream\"/>";
        assertEquals(new Software("walness", null), xstream.fromXML(xml));
        assertEquals(new Software("walness", "xstream"), xstream.fromXML(xml.replace("name", "id")));
    }

    public void testForReferenceSystemAttribute() {
        final List<Software> list = new ArrayList<>();
        final Software software = new Software("walness", "xstream");
        list.add(software);
        list.add(software);

        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("refid", "reference");

        final String xml = ""
            + "<list>\n"
            + "  <software vendor=\"walness\" name=\"xstream\"/>\n"
            + "  <software refid=\"../software\"/>\n"
            + "</list>";

        assertBothWays(list, xml);
    }

    public void testForSystemAttributes() {
        final List<Category<?>> list = new LinkedList<>();
        final Category<Category<?>> category = new Category<>("walness", "xstream");
        category.setProducts(list);
        list.add(category);

        xstream.alias("category", Category.class);
        xstream.useAttributeFor(Category.class, "id");
        xstream.aliasAttribute("class", "id");
        xstream.aliasSystemAttribute("type", "class");
        xstream.aliasSystemAttribute("refid", "reference");

        final String xml = ""
            + "<category class=\"xstream\">\n"
            + "  <name>walness</name>\n"
            + "  <products type=\"linked-list\">\n"
            + "    <category refid=\"../..\"/>\n"
            + "  </products>\n"
            + "</category>";

        assertBothWays(category, xml);
    }

    public void testIdentityForFields() {
        final Software software = new Software("walness", "xstream");

        xstream.alias("software", Software.class);
        xstream.aliasField("name", Software.class, "name");
        xstream.aliasField("vendor", Software.class, "vendor");

        final String xml = ""
            + "<software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n"
            + "</software>";

        assertBothWays(software, xml);
    }

    static class FieldsWithInternalNames {
        String clazz;
        String ref;
    }

    public void testCanUseInternalNameAsFieldAlias() {
        final FieldsWithInternalNames object = new FieldsWithInternalNames();
        object.clazz = "TestIt";
        object.ref = "MyReference";

        xstream.alias("internalNames", FieldsWithInternalNames.class);
        xstream.aliasField("class", FieldsWithInternalNames.class, "clazz");
        xstream.aliasField("reference", FieldsWithInternalNames.class, "ref");

        final String xml = ""
            + "<internalNames>\n"
            + "  <class>TestIt</class>\n"
            + "  <reference>MyReference</reference>\n"
            + "</internalNames>";

        assertBothWays(object, xml);
    }

    public void testCanAliasPrimitiveTypes() {
        final Object object = new boolean[]{true, false};
        xstream.alias("bo", Boolean.TYPE);
        final String xml = "" //
            + "<bo-array>\n"
            + "  <bo>true</bo>\n"
            + "  <bo>false</bo>\n"
            + "</bo-array>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArray() {
        final Object object = new boolean[]{true, false};
        xstream.alias("boa", boolean[].class);
        final String xml = ""//
            + "<boa>\n"
            + "  <boolean>true</boolean>\n"
            + "  <boolean>false</boolean>\n"
            + "</boa>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArrayInMultiDimension() {
        final Object object = new boolean[][]{{true, false}};
        xstream.alias("boa", boolean[].class);
        final String xml = ""
            + "<boa-array>\n"
            + "  <boa>\n"
            + "    <boolean>true</boolean>\n"
            + "    <boolean>false</boolean>\n"
            + "  </boa>\n"
            + "</boa-array>";
        assertBothWays(object, xml);
    }

    public static class TypeA {
        final String attrA = "testA";
    }

    public static class TypeB extends TypeA {
        final String attrB = "testB";
    }

    public static class TypeC extends TypeB {
        final String attrC = "testC";
    }

    public void testCanAliasInheritedFields() {
        xstream.alias("test", TypeC.class);
        xstream.aliasField("a", TypeA.class, "attrA");
        xstream.aliasField("b", TypeB.class, "attrB");
        xstream.aliasField("c", TypeC.class, "attrC");
        final TypeC object = new TypeC();
        final String xml = "" //
            + "<test>\n"
            + "  <a>testA</a>\n"
            + "  <b>testB</b>\n"
            + "  <c>testC</c>\n"
            + "</test>";
        assertBothWays(object, xml);
    }

    public void testCanDeserializeAliasedInheritedFieldsToSameName() {
        xstream.alias("test", TypeC.class);
        xstream.alias("A", TypeA.class);
        xstream.alias("B", TypeB.class);
        xstream.aliasField("attr", TypeA.class, "attrA");
        xstream.aliasField("attr", TypeB.class, "attrB");
        xstream.aliasField("attr", TypeC.class, "attrC");
        final TypeC object = new TypeC();
        final String xml = ""
            + "<test>\n"
            + "  <attr defined-in=\"A\">testA</attr>\n"
            + "  <attr defined-in=\"B\">testB</attr>\n"
            + "  <attr>testC</attr>\n"
            + "</test>";
        assertObjectsEqual(object, xstream.fromXML(xml));
        // assertBothWays(object, xml);
    }

    public void testCanOverwriteInheritedAlias() {
        xstream.alias("test", TypeC.class);
        xstream.aliasField("a", TypeA.class, "attrA");
        xstream.aliasField("b", TypeB.class, "attrB");
        xstream.aliasField("c", TypeC.class, "attrC");
        xstream.aliasField("y", TypeC.class, "attrA");
        final TypeC object = new TypeC();
        final String xml = "" + "<test>\n" + "  <y>testA</y>\n" + "  <b>testB</b>\n" + "  <c>testC</c>\n" + "</test>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArrayElements() {
        final Object[] software = new Object[]{new Software("walness", "xstream")};

        xstream.alias("software", Software.class);
        xstream.aliasField("Name", Software.class, "name");
        xstream.aliasField("Vendor", Software.class, "vendor");

        final String xml = ""
            + "<object-array>\n"
            + "  <software>\n"
            + "    <Vendor>walness</Vendor>\n"
            + "    <Name>xstream</Name>\n"
            + "  </software>\n"
            + "</object-array>";

        assertBothWays(software, xml);
    }

    public void testCanAliasCompletePackage() {
        final Software software = new Software("walness", "xstream");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks.acceptance.objects");

        final String xml = ""
            + "<org.codehaus.Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n"
            + "</org.codehaus.Software>";

        assertBothWays(software, xml);
    }

    public void testCanAliasSubPackage() {
        final Software software = new Software("walness", "xstream");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");

        final String xml = ""
            + "<org.codehaus.acceptance.objects.Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n"
            + "</org.codehaus.acceptance.objects.Software>";

        assertBothWays(software, xml);
    }

    public void testToDefaultPackage() {
        final Software software = new Software("walness", "xstream");
        xstream.aliasPackage("", "com.thoughtworks.acceptance.objects");

        final String xml = ""
            + "<Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n"
            + "</Software>";

        assertBothWays(software, xml);
    }

    public void testForLongerPackageNameTakesPrecedence() {
        final WithList<Object> withList = new WithList<>();
        withList.things.add(new Software("walness", "xstream"));
        withList.things.add(new TypeA());
        xstream.aliasPackage("model", "com.thoughtworks.acceptance.objects");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");
        xstream.aliasPackage("model.foo", "com.thoughtworks.acceptance.someobjects");

        final String xml = ""
            + "<model.foo.WithList>\n"
            + "  <things>\n"
            + "    <model.Software>\n"
            + "      <vendor>walness</vendor>\n"
            + "      <name>xstream</name>\n"
            + "    </model.Software>\n"
            + "    <org.codehaus.acceptance.AliasTest_-TypeA>\n"
            + "      <attrA>testA</attrA>\n"
            + "    </org.codehaus.acceptance.AliasTest_-TypeA>\n"
            + "  </things>\n"
            + "</model.foo.WithList>";

        assertBothWays(withList, xml);
    }

    public void testClassTakesPrecedenceOfPackage() {
        final WithList<Software> withList = new WithList<>();
        withList.things.add(new Software("walness", "xstream"));
        xstream.alias("MySoftware", Software.class);
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");
        xstream.aliasPackage("model.foo", "com.thoughtworks.acceptance.someobjects");

        final String xml = ""
            + "<model.foo.WithList>\n"
            + "  <things>\n"
            + "    <MySoftware>\n"
            + "      <vendor>walness</vendor>\n"
            + "      <name>xstream</name>\n"
            + "    </MySoftware>\n"
            + "  </things>\n"
            + "</model.foo.WithList>";

        assertBothWays(withList, xml);
    }

    @SuppressWarnings("unused")
    private void takingDoubles(final Double d1, final double d2) {
    }

    public void testCanCreateAliasingJavaTypeConverter() throws NoSuchFieldException, NoSuchMethodException {
        final Mapper mapper = new MapperWrapper(xstream.getMapper().lookupMapperOfType(ArrayMapper.class)) {
            @Override
            public Class<?> realClass(final String elementName) {
                final Class<?> primitiveType = Primitives.primitiveType(elementName);
                return primitiveType != null ? primitiveType : super.realClass(elementName);
            }
        };
        final SingleValueConverter javaClassConverter = new JavaClassConverter(mapper) {};
        xstream.registerConverter(javaClassConverter);
        xstream.registerConverter(new JavaMethodConverter(javaClassConverter) {});
        xstream.registerConverter(new JavaFieldConverter(javaClassConverter, mapper) {});
        xstream.alias("A", TypeA.class);
        xstream.alias("Prod", Product.class);
        xstream.aliasField("a", TypeA.class, "attrA");
        xstream.alias("Test", getClass());

        final List<Object> list = new ArrayList<>();
        list.add(TypeA.class);
        list.add(int[][][].class);
        list.add(Integer[][][].class);
        list.add(TypeA.class.getDeclaredField("attrA"));
        list.add(Product.class.getConstructor(String.class, String.class, double.class));
        list.add(getClass().getDeclaredMethod("takingDoubles", Double.class, double.class));
        list.add(ArrayList.class);

        final String xml = ""
            + "<list>\n"
            + "  <java-class>A</java-class>\n"
            + "  <java-class>int-array-array-array</java-class>\n"
            + "  <java-class>java.lang.Integer-array-array-array</java-class>\n"
            + "  <field>\n"
            + "    <name>a</name>\n"
            + "    <clazz>A</clazz>\n"
            + "  </field>\n"
            + "  <constructor>\n"
            + "    <class>Prod</class>\n"
            + "    <parameter-types>\n"
            + "      <class>string</class>\n"
            + "      <class>string</class>\n"
            + "      <class>double</class>\n"
            + "    </parameter-types>\n"
            + "  </constructor>\n"
            + "  <method>\n"
            + "    <class>Test</class>\n"
            + "    <name>takingDoubles</name>\n"
            + "    <parameter-types>\n"
            + "      <class>java.lang.Double</class>\n"
            + "      <class>double</class>\n"
            + "    </parameter-types>\n"
            + "  </method>\n"
            + "  <java-class>java.util.ArrayList</java-class>\n"
            + "</list>";

        assertBothWays(list, xml);
    }

    public static class Protocols {
        static class HTTP extends Protocol {
            public HTTP() {
                super("http");
            }
        }

        static class TCP extends Protocol {
            public TCP() {
                super("tcp");
            }
        }

        static class UDP extends Protocol {
            public UDP() {
                super("udp");
            }
        }

        Object http;
        TCP tcp;
        Protocol udp;
    }

    public void testErasesSystemAttribute() {
        xstream.alias("protocols", Protocols.class);
        xstream.aliasSystemAttribute(null, "class");

        final Protocols exceptions = new Protocols();
        exceptions.http = new Protocols.HTTP();
        exceptions.tcp = new Protocols.TCP();
        exceptions.udp = new Protocols.UDP();

        final String expected = ""
            + "<protocols>\n"
            + "  <http>\n"
            + "    <id>http</id>\n"
            + "  </http>\n"
            + "  <tcp>\n"
            + "    <id>tcp</id>\n"
            + "  </tcp>\n"
            + "  <udp>\n"
            + "    <id>udp</id>\n"
            + "  </udp>\n"
            + "</protocols>";
        assertEquals(expected, xstream.toXML(exceptions));
    }
}
