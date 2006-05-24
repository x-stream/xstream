package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.Handler;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter2Test;

import javax.xml.namespace.QName;

import java.util.ArrayList;

public class QNameMappedConcreteClassesTest extends AbstractAcceptanceTest {

    public static final String XML_HEADER = StaxWriter2Test.XML_HEADER;

    protected QNameMap qnameMap;
    protected String namespace = getDefaultNS(WithList.class);

    public void testUsingNamespace() {
        // lets register some qnames
        QName qname = new QName(namespace, "withList", "w");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<things></things>" +
                "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespace() {
        qnameMap.setDefaultNamespace(namespace);
        xstream.alias("withList", WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<withList xmlns=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<things></things>" +
                "</withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespaceAndPrefix() {
        qnameMap.setDefaultNamespace(namespace);
        qnameMap.setDefaultPrefix("x");
        QName qname = new QName(namespace, "withList", "x");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<x:withList xmlns:x=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<x:things></x:things>" +
                "</x:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespaces() {
        // lets register some qnames
        qnameMap.registerMapping(new QName(namespace, "withList", "w"), WithList.class);
        qnameMap.registerMapping(new QName("urn:foo", "things", "f"), "things");

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<f:things xmlns:f=\"urn:foo\"></f:things>" +
                "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespacesWithAliases() {
        xstream.alias("handler", X.class);
        xstream.alias("protocol", Y.class);

        qnameMap.registerMapping(new QName(getDefaultNS(Handler.class)+1, "handler", "h"), "handler");
        qnameMap.registerMapping(new QName(getDefaultNS(Protocol.class)+2, "protocol", "p"), "innerObj");

        X x = new X();
        x.aStr = "foo";
        x.anInt = 42;
        x.innerObj = new Y();
        x.innerObj.yField = "YField";

        String expected =
                XML_HEADER +
                "<h:handler xmlns:h=\"java://com.thoughtworks.acceptance.someobjects1\">" +
                "<aStr>foo</aStr>" +
                "<anInt>42</anInt>" +
                "<p:protocol xmlns:p=\"java://com.thoughtworks.acceptance.someobjects2\">" +
                "<yField>YField</yField>" +
                "</p:protocol>" +
                "</h:handler>";

        assertBothWays(x, expected);
    }

    protected HierarchicalStreamDriver createDriver() {
        // careful, called from inside base class constructor
        qnameMap = new QNameMap();
        return new StaxDriver(qnameMap);
    }

    protected String getDefaultNS(Class type) {
        return "java://" + type.getPackage().getName();
    }
}
