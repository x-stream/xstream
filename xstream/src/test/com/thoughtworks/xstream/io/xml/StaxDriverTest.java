package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * @author J&ouml;rg Schaible
 */
public class StaxDriverTest extends AbstractAcceptanceTest {
    private static class MyStaxDriver extends StaxDriver {
        public boolean createStaxWriterCalled = false;
        public boolean createStaxReaderCalled = false;

        public StaxWriter createStaxWriter(XMLStreamWriter out) throws StreamException {
            createStaxWriterCalled = true;
            try {
                return super.createStaxWriter(out);
            } catch (XMLStreamException e) {
                throw new StreamException(e);
            }
        }

        public AbstractPullReader createStaxReader(XMLStreamReader in) {
            createStaxReaderCalled = true;
            return super.createStaxReader(in);
        }
    }

    public void testCanOverloadStaxReaderAndWriterInstantiation() {
        final MyStaxDriver driver = new MyStaxDriver();
        xstream = new XStream(driver);
        assertBothWays("Hi", StaxWriter2Test.XML_HEADER + "<string>Hi</string>");
        assertTrue(driver.createStaxReaderCalled);
        assertTrue(driver.createStaxWriterCalled);
    }
}
