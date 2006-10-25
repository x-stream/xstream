package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import org.apache.oro.text.perl.Perl5Util;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;

import java.io.StringWriter;

/*
 * @author James Strachan
 */

public class StaxWriter2Test extends AbstractXMLWriterTest {

    // For WoodStox
    //public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

    // For RI
    public static final String XML_HEADER = "<?xml version='1.0' encoding='utf-8'?>";

    private StringWriter buffer;
    private XMLOutputFactory outputFactory;
    private X testInput;
    private Perl5Util perlUtil;

    protected void setUp() throws Exception {
        super.setUp();
        outputFactory = XMLOutputFactory.newInstance();
        buffer = new StringWriter();
        writer = new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
        perlUtil = new Perl5Util();

        testInput = new X();
        testInput.anInt = 9;
        testInput.aStr = "zzz";
        testInput.innerObj = new Y();
        testInput.innerObj.yField = "ooo";
    }

    protected void assertXmlProducedIs(String expected) {
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#x0D;", "&#13;");
        expected = "<?xml version='1.0' encoding='utf-8'?>" + expected;
        assertEquals(expected, buffer.toString());
    }

    public void testNamespacedXmlWithPrefix() throws Exception {
        QNameMap qnameMap = new QNameMap();
        QName qname = new QName("http://foo.com", "alias", "foo");
        qnameMap.registerMapping(qname, X.class);

        String expected = XML_HEADER + "<foo:alias xmlns:foo=\"http://foo.com\"><aStr>zzz</aStr><anInt>9</anInt><innerObj><yField>ooo</yField></innerObj></foo:alias>";

        marshalWithBothRepairingModes(qnameMap, expected);
    }

    public void testNamespacedXmlWithoutPrefix() throws Exception {
        QNameMap qnameMap = new QNameMap();
        QName qname = new QName("http://foo.com", "bar");
        qnameMap.registerMapping(qname, X.class);

        String expected = XML_HEADER + "<bar xmlns=\"http://foo.com\"><aStr>zzz</aStr><anInt>9</anInt><innerObj><yField>ooo</yField></innerObj></bar>";

        marshalWithBothRepairingModes(qnameMap, expected);

    }

    protected void marshalWithBothRepairingModes(QNameMap qnameMap, String expected) {
        String text = marshall(qnameMap, true);
        assertEquals("Generated XML with repairing mode: true", expected, text);

        text = marshall(qnameMap, false);
        assertEquals("Generated XML with repairing mode: false", expected, text);
    }

    protected String marshall(QNameMap qnameMap, boolean repairNamespaceMode) {
        StaxDriver staxDriver = new StaxDriver(qnameMap);
        staxDriver.setRepairingNamespace(repairNamespaceMode);
        XStream xstream = new XStream(staxDriver);
        return xstream.toXML(testInput);
    }

}

