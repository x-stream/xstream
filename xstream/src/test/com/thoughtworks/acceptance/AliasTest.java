/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 */
public class AliasTest extends AbstractAcceptanceTest {

    public void testBarfsIfItDoesNotExist() {

        String xml = "" +
                "<X-array>\n" +
                "  <X>\n" +
                "    <anInt>0</anInt>\n" +
                "  </X>\n" +
                "</X-array>";

        // now change the alias
        xstream.alias("Xxxxxxxx", X.class);
        try {
            xstream.fromXML(xml);
            fail("ShouldCannotResolveClassException expected");
        } catch (CannotResolveClassException expectedException) {
            // expected
        }
    }
    
    public void testWithUnderscore() {
        xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
        String xml = "" +
                "<X_alias>\n" +
                "  <anInt>0</anInt>\n" +
                "</X_alias>";

        // now change the alias
        xstream.alias("X_alias", X.class);
        X x = new X(0);
        assertBothWays(x, xml);
    }

    public static class HasUnderscore {
        private String _attr = "foo";
    }

    public void testWithPrefixedUnderscore(){
        HasUnderscore x = new HasUnderscore();

        xstream.alias("underscore", HasUnderscore.class);
        xstream.aliasField("attr", HasUnderscore.class, "_attr");

        String xml = "" + 
            "<underscore>\n" + 
            "  <attr>foo</attr>\n" + 
            "</underscore>";

        assertBothWays(x, xml);
    }
    
    public void testForFieldAsAttribute() {
        Software software = new Software("walness", "xstream");
        
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("id", "name");
        
        String xml = "<software vendor=\"walness\" id=\"xstream\"/>";
        
        assertBothWays(software, xml);
    }
    
    public void testForReferenceSystemAttribute() {
        List list = new ArrayList();
        Software software = new Software("walness", "xstream");
        list.add(software);
        list.add(software);
        
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("refid", "reference");
        
        String xml = "" + 
            "<list>\n" +
            "  <software vendor=\"walness\" name=\"xstream\"/>\n" +
            "  <software refid=\"../software\"/>\n" +
            "</list>";
        
        assertBothWays(list, xml);
    }
    
    public void testForSystemAttributes() {
        List list = new LinkedList();
        Category category = new Category("walness", "xstream");
        category.setProducts(list);
        list.add(category);
        
        xstream.alias("category", Category.class);
        xstream.useAttributeFor(Category.class, "id");
        xstream.aliasAttribute("class", "id");
        xstream.aliasSystemAttribute("type", "class");
        xstream.aliasSystemAttribute("refid", "reference");
        
        String xml = "" + 
            "<category class=\"xstream\">\n" +
            "  <name>walness</name>\n" +
            "  <products type=\"linked-list\">\n" +
            "    <category refid=\"../..\"/>\n" +
            "  </products>\n" +
            "</category>";
        
        assertBothWays(category, xml);
    }
    
    public void testIdentityForFields() {
        Software software = new Software("walness", "xstream");

        xstream.alias("software", Software.class);
        xstream.aliasField("name", Software.class, "name");
        xstream.aliasField("vendor", Software.class, "vendor");
        
        String xml = ""
            + "<software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n" 
            + "</software>";
        
        assertBothWays(software, xml);
    }
    
    private static class FieldsWithInternalNames {
        String clazz;
        String ref;
    }

    public void testCanUseInternalNameAsFieldAlias() {
        FieldsWithInternalNames object = new FieldsWithInternalNames();
        object.clazz = "TestIt";
        object.ref = "MyReference";

        xstream.alias("internalNames", FieldsWithInternalNames.class);
        xstream.aliasField("class", FieldsWithInternalNames.class, "clazz");
        xstream.aliasField("reference", FieldsWithInternalNames.class, "ref");
        
        String xml = ""
                + "<internalNames>\n"
                + "  <class>TestIt</class>\n"
                + "  <reference>MyReference</reference>\n"
                + "</internalNames>";

        assertBothWays(object, xml);
    }
    
    public void testCanAliasPrimitiveTypes() {
        Object object = new boolean[]{true, false};
        xstream.alias("bo", Boolean.TYPE);
        String xml = ""
            + "<bo-array>\n"
            + "  <bo>true</bo>\n"
            + "  <bo>false</bo>\n"
            + "</bo-array>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArray() {
        Object object = new boolean[]{true, false};
        xstream.alias("boa", boolean[].class);
        String xml = ""
            + "<boa>\n"
            + "  <boolean>true</boolean>\n"
            + "  <boolean>false</boolean>\n"
            + "</boa>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArrayInMultiDimension() {
        Object object = new boolean[][]{{true, false}};
        xstream.alias("boa", boolean[].class);
        String xml = ""
            + "<boa-array>\n"
            + "  <boa>\n"
            + "    <boolean>true</boolean>\n"
            + "    <boolean>false</boolean>\n"
            + "  </boa>\n"
            + "</boa-array>";
        assertBothWays(object, xml);
    }

    public static class TypeA {
        private String attrA = "testA";
    }

    public static class TypeB extends TypeA {
        private String attrB = "testB";
    }

    public static class TypeC extends TypeB {
        private String attrC = "testC";
    }

    public void testCanAliasInheritedFields() {
        xstream.alias("test", TypeC.class);
        xstream.aliasField("a", TypeA.class, "attrA");
        xstream.aliasField("b", TypeB.class, "attrB");
        xstream.aliasField("c", TypeC.class, "attrC");
        TypeC object = new TypeC();
        String xml = ""
            + "<test>\n"
            + "  <a>testA</a>\n"
            + "  <b>testB</b>\n"
            + "  <c>testC</c>\n"
            + "</test>";
        assertBothWays(object, xml);
    }

    public void testCanOverwriteInheritedAlias() {
        xstream.alias("test", TypeC.class);
        xstream.aliasField("a", TypeA.class, "attrA");
        xstream.aliasField("b", TypeB.class, "attrB");
        xstream.aliasField("c", TypeC.class, "attrC");
        xstream.aliasField("y", TypeC.class, "attrA");
        TypeC object = new TypeC();
        String xml = ""
            + "<test>\n"
            + "  <y>testA</y>\n"
            + "  <b>testB</b>\n"
            + "  <c>testC</c>\n"
            + "</test>";
        assertBothWays(object, xml);
    }

    public void testCanAliasArrayElements() {
        Object[] software = new Object[]{new Software("walness", "xstream")};

        xstream.alias("software", Software.class);
        xstream.aliasField("Name", Software.class, "name");
        xstream.aliasField("Vendor", Software.class, "vendor");
        
        String xml = "" //
            + "<object-array>\n"
            + "  <software>\n"
            + "    <Vendor>walness</Vendor>\n"
            + "    <Name>xstream</Name>\n" 
            + "  </software>\n"
            + "</object-array>";
        
        assertBothWays(software, xml);
    }
    
    public void testCanAliasCompletePackage() {
        Software software = new Software("walness", "xstream");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks.acceptance.objects");
        
        String xml = "" //
            + "<org.codehaus.Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n" 
            + "</org.codehaus.Software>";
        
        assertBothWays(software, xml);
    }
    
    public void testCanAliasSubPackage() {
        Software software = new Software("walness", "xstream");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");
        
        String xml = "" //
            + "<org.codehaus.acceptance.objects.Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n" 
            + "</org.codehaus.acceptance.objects.Software>";
        
        assertBothWays(software, xml);
    }
    
    public void testToDefaultPackage() {
        Software software = new Software("walness", "xstream");
        xstream.aliasPackage("", "com.thoughtworks.acceptance.objects");
        
        String xml = "" //
            + "<Software>\n"
            + "  <vendor>walness</vendor>\n"
            + "  <name>xstream</name>\n" 
            + "</Software>";
        
        assertBothWays(software, xml);
    }
    
    public void testForLongerPackageNameTakesPrecedence() {
        WithList withList = new WithList();
        withList.things.add(new Software("walness", "xstream"));
        withList.things.add(new TypeA());
        xstream.aliasPackage("model", "com.thoughtworks.acceptance.objects");
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");
        xstream.aliasPackage("model.foo", "com.thoughtworks.acceptance.someobjects");
        
        String xml = "" //
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
        WithList withList = new WithList();
        withList.things.add(new Software("walness", "xstream"));
        xstream.alias("MySoftware", Software.class);
        xstream.aliasPackage("org.codehaus", "com.thoughtworks");
        xstream.aliasPackage("model.foo", "com.thoughtworks.acceptance.someobjects");
        
        String xml = "" //
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
}
