package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.ArrayList;

public class QNameMappedConcreteClassesTest extends ConcreteClassesTest {

    protected QNameMap qnameMap;
    protected StaxDriver staxDriver;
    protected String namespace = "java://" + WithList.class.getPackage().getName();

    public void testDefaultImplementationOfInterface() {
        // lets register some qnames
        QName qname = new QName(namespace, "withList", "w");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                "<?xml version='1.0' encoding='utf-8'?><w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
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
                "<?xml version='1.0' encoding='utf-8'?><withList xmlns=\"java://com.thoughtworks.acceptance.someobjects\">" +
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
                "<?xml version='1.0' encoding='utf-8'?><x:withList xmlns:x=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<x:things></x:things>" +
                "</x:withList>";

        assertBothWays(withList, expected);
    }

    public void testAlternativeImplementationOfInterface() {
        /** TODO temporarily disabled due to StAX RI bug that should be fixed real soon */
    }

    protected HierarchicalStreamDriver createDriver() {
        // careful, called from inside base class constructor
        qnameMap = new QNameMap();
        staxDriver = new StaxDriver(qnameMap);
        return staxDriver;
    }

    protected String toXML(Object root) {
        StringWriter buffer = new StringWriter();
        xstream.marshal(root, staxDriver.createWriter(buffer));
        return buffer.toString();
    }

}
