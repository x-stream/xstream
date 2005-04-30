package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;

public class QNameMappedConcreteClassesTest extends ConcreteClassesTest {

    // For WoodStox
    //public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

    // For RI
    public static final String XML_HEADER = "<?xml version='1.0' encoding='utf-8'?>";

    protected QNameMap qnameMap;
    protected HierarchicalStreamDriver staxDriver;
    protected String namespace = "java://" + WithList.class.getPackage().getName();

    public void testDefaultImplementationOfInterface() {
        // lets register some qnames
        QName qname = new QName(namespace, "withList", "w");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER + "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
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
                XML_HEADER + "<withList xmlns=\"java://com.thoughtworks.acceptance.someobjects\">" +
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
                XML_HEADER + "<x:withList xmlns:x=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<x:things></x:things>" +
                "</x:withList>";

        assertBothWays(withList, expected);
    }

    public void testAlternativeImplementationOfInterface() {
        xstream.alias("with-list", WithList.class);
        xstream.alias("linked-list", LinkedList.class);

        WithList withList = new WithList();
        withList.things = new LinkedList();

        String expected =
                "<?xml version='1.0' encoding='utf-8'?><with-list><things class=\"linked-list\"></things></with-list>";

        assertBothWays(withList, expected);
    }

    public void testCustomInterfaceCanHaveMultipleImplementations() {
        xstream.alias("intf", MyInterface.class);
        xstream.alias("imp1", MyImp1.class);
        xstream.alias("imp2", MyImp2.class);
        xstream.alias("h", MyHolder.class);

        MyHolder in = new MyHolder();
        in.field1 = new MyImp1();
        in.field2 = new MyImp2();

        String expected = "" +
                "<?xml version='1.0' encoding='utf-8'?><h><field1 class=\"imp1\"><x>1</x></field1><field2 class=\"imp2\"><y>2</y></field2></h>";

        String xml = xstream.toXML(in);
        assertEquals(expected, xml);

        MyHolder out = (MyHolder) xstream.fromXML(xml);
        assertEquals(MyImp1.class, out.field1.getClass());
        assertEquals(MyImp2.class, out.field2.getClass());
        assertEquals(2, ((MyImp2) out.field2).y);
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
