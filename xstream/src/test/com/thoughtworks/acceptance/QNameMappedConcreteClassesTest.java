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
    private StaxDriver staxDriver;

    public void testDefaultImplementationOfInterface() {

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                "<?xml version='1.0' encoding='utf-8'?><w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<things></things>" +
                "</w:withList>";

        assertBothWays(withList, expected);

    }

    public void testAlternativeImplementationOfInterface() {
        /** TODO temporarily disabled due to StAX RI bug that should be fixed real soon */
    }

    protected HierarchicalStreamDriver createDriver() {
        // careful, called from inside base class constructor
        qnameMap = new QNameMap();

        // lets register some qnames
        String namespace = "java://" + WithList.class.getPackage().getName();
        QName qname = new QName(namespace, "withList", "w");
        System.out.println("Mapping the WithList class to: " + qname);
        qnameMap.registerMapping(qname, WithList.class);
        staxDriver = new StaxDriver(qnameMap);
        return staxDriver;
    }

    protected String toXML(Object root) {
        StringWriter buffer = new StringWriter();
        xstream.marshal(root, staxDriver.createWriter(buffer));
        return buffer.toString();
    }

}
