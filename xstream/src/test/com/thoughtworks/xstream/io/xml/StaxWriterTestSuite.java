package com.thoughtworks.xstream.io.xml;

import com.bea.xml.stream.XMLOutputFactoryBase;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.oro.text.perl.Perl5Util;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.StringWriter;


/*
 * @author James Strachan
 * @author J&ouml;rg Schaible
 */

public class StaxWriterTestSuite extends TestSuite {

    public StaxWriterTestSuite() {
        super(StaxWriterTestSuite.class.getName());
        addBeaRITestSuite();
        addWoodstoxTestSuite();
        addJDK6TestSuite();
    }

    public static Test suite() {
        return new StaxWriterTestSuite();
    }

    private void addBeaRITestSuite() {
        addTest(new TestSuite(BEA.class, "BEA"));
    }

    private void addWoodstoxTestSuite() {
        addTest(new TestSuite(Woodstox.class, "Woodstox"));
    }

    private void addJDK6TestSuite() {
        try {
            Class.forName(JDK6.className);
            addTest(new TestSuite(JDK6.class, "JDK6"));
        } catch (ClassNotFoundException e) {
            if (JVM.is16()) {
                throw new AssertionFailedError("Cannot instantiate " + JDK6.className);
            }
        }
    }

    public final static class BEA extends StaxWriterTest {
        public BEA() {
            System.setProperty(XMLOutputFactory.class.getName(), XMLOutputFactoryBase.class
                .getName());
        }

        protected void assertXmlProducedIs(String expected) {
            if (outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES).equals(Boolean.FALSE)) {
                expected = perlUtil.substitute("s#<(\\w+|\\w+:\\w+) (xmlns[^\"]*\"[^\"]*\")>#<$1>#g", expected);
            } else {
                expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
            }
            expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
            expected = replaceAll(expected, "&#x0D;", "&#13;");
            expected = getXMLHeader() + expected;
            assertEquals(expected, buffer.toString());
        }

        protected String getXMLHeader() {
            return "<?xml version='1.0' encoding='utf-8'?>";
        }

        protected XMLOutputFactory getOutputFactory() {
            return new XMLOutputFactoryBase();
        }
    }

    public final static class Woodstox extends StaxWriterTest {
        public Woodstox() {
            System.setProperty(XMLOutputFactory.class.getName(), WstxOutputFactory.class
                .getName());
        }

        protected void assertXmlProducedIs(String expected) {
            if (outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES).equals(Boolean.FALSE)) {
                expected = perlUtil.substitute("s#<(\\w+|\\w+:\\w+) (xmlns[^\"]*\"[^\"]*\")>#<$1>#g", expected);
            } else if(perlUtil.match("#<\\w+:\\w+(>| xmlns:\\w+=)#", expected)) {
                expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
            }
            expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2 />#g", expected);
            expected = replaceAll(expected, "&#x0D;", "&#xd;");
            expected = replaceAll(expected, "&gt;", ">"); // Woodstox bug !!
            expected = getXMLHeader() + expected;
            assertEquals(expected, buffer.toString());
        }

        protected String getXMLHeader() {
            return "<?xml version='1.0' encoding='UTF-8'?>";
        }

        protected XMLOutputFactory getOutputFactory() {
            return new WstxOutputFactory();
        }
    }

    public final static class JDK6 extends StaxWriterTest {
        private final static String className = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";
        private final Class factoryClass;

        protected void assertXmlProducedIs(String expected) {
            if (outputFactory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES).equals(Boolean.FALSE)) {
                expected = perlUtil.substitute("s#<(\\w+|\\w+:\\w+) (xmlns[^\"]*\"[^\"]*\")>#<$1>#g", expected);
            }
            expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
            expected = replaceAll(expected, "&#x0D;", "\r");
            expected = getXMLHeader() + expected;
            assertEquals(expected, buffer.toString());
        }

        public JDK6() {
            try {
                this.factoryClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new AssertionFailedError("Cannot load JDK 6 class " + className);
            }
            System.setProperty(XMLOutputFactory.class.getName(), className);
        }

        protected String getXMLHeader() {
            return "<?xml version=\"1.0\" ?>";
        }

        protected XMLOutputFactory getOutputFactory() {
            try {
                return (XMLOutputFactory)this.factoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new AssertionFailedError("Cannot instantiate " + className);
            } catch (IllegalAccessException e) {
                throw new AssertionFailedError("Cannot access default ctor of " + className);
            }
        }
    }

    protected static abstract class StaxWriterTest extends AbstractXMLWriterTest {

        protected StringWriter buffer;
        protected Perl5Util perlUtil;
        protected XMLOutputFactory outputFactory;
        private X testInput;

        protected abstract String getXMLHeader();

        protected abstract XMLOutputFactory getOutputFactory();

        protected void setUp() throws Exception {
            super.setUp();
            outputFactory = getOutputFactory();
            outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
            buffer = new StringWriter();
            writer = new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
            perlUtil = new Perl5Util();

            testInput = new X();
            testInput.anInt = 9;
            testInput.aStr = "zzz";
            testInput.innerObj = new Y();
            testInput.innerObj.yField = "ooo";
        }

        public void testNamespacedXmlWithPrefix() throws Exception {
            QNameMap qnameMap = new QNameMap();
            QName qname = new QName("http://foo.com", "alias", "foo");
            qnameMap.registerMapping(qname, X.class);

            String expected = "<foo:alias xmlns:foo=\"http://foo.com\"><aStr xmlns=\"\">zzz</aStr><anInt xmlns=\"\">9</anInt><innerObj xmlns=\"\"><yField>ooo</yField></innerObj></foo:alias>";
            marshalWithBothRepairingModes(qnameMap, expected);
        }

        public void testNamespacedXmlWithoutPrefix() throws Exception {
            QNameMap qnameMap = new QNameMap();
            QName qname = new QName("http://foo.com", "bar");
            qnameMap.registerMapping(qname, X.class);

            String expected = "<bar xmlns=\"http://foo.com\"><aStr xmlns=\"\">zzz</aStr><anInt xmlns=\"\">9</anInt><innerObj xmlns=\"\"><yField>ooo</yField></innerObj></bar>";
            marshalWithBothRepairingModes(qnameMap, expected);
        }

        protected void marshalWithBothRepairingModes(QNameMap qnameMap, String expected)
                                                                                        throws XMLStreamException {
            marshall(qnameMap, true);
            assertXmlProducedIs(expected);

            marshall(qnameMap, false);
            assertXmlProducedIs(expected);
        }

        protected void marshall(QNameMap qnameMap, boolean repairNamespaceMode)
                                                                                 throws XMLStreamException {
            outputFactory.setProperty(
                XMLOutputFactory.IS_REPAIRING_NAMESPACES, repairNamespaceMode
                    ? Boolean.TRUE
                    : Boolean.FALSE);
            XStream xstream = new XStream((HierarchicalStreamDriver)null);
            buffer = new StringWriter();
            XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(buffer);
            xstream.marshal(testInput, new StaxWriter(qnameMap, xmlStreamWriter));
        }

    }
}
