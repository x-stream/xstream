package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Hammant
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
    
    public void testForFieldAsAttribute() {
        Software software = new Software("walness", "xstream");
        
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("id", "name");
        
        String xml = "<software vendor=\"walness\" id=\"xstream\"/>";
        
        assertBothWays(software, xml);
    }
    
    public void testForReferenceAttribute() {
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

    public void testCanAliasArrayElements() {
        Object[] software = new Object[]{new Software("walness", "xstream")};

        xstream.alias("software", Software.class);
        xstream.aliasField("Name", Software.class, "name");
        xstream.aliasField("Vendor", Software.class, "vendor");
        
        String xml = ""
            + "<object-array>\n"
            + "  <software>\n"
            + "    <Vendor>walness</Vendor>\n"
            + "    <Name>xstream</Name>\n" 
            + "  </software>\n"
            + "</object-array>";
        
        assertBothWays(software, xml);
    }
}
