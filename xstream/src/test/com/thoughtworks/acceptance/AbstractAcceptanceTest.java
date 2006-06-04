package com.thoughtworks.acceptance;

import java.lang.reflect.Array;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.xml.XppDriver;

public abstract class AbstractAcceptanceTest extends TestCase {

    protected transient XStream xstream = createXStream();
    
    protected XStream createXStream() {
        return new XStream(createDriver());
    }

    protected HierarchicalStreamDriver createDriver() {
        // if the system property is set, use it to load the driver
        String driver = null;
        try {
            driver = System.getProperty("xstream.driver");
            if (driver != null) {
                System.out.println("Using driver: " + driver);
                Class type = Class.forName(driver);
                return (HierarchicalStreamDriver) type.newInstance();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load driver: " + driver);
        }
        return new XppDriver();
    }

    protected Object assertBothWays(Object root, String xml) {

        // First, serialize the object to XML and check it matches the expected XML.
        String resultXml = toXML(root);
        assertEquals(xml, resultXml);

        // Now deserialize the XML back into the object and check it equals the original object.
        Object resultRoot = xstream.fromXML(resultXml);
        assertObjectsEqual(root, resultRoot);

        // While we're at it, let's check the binary serialization works...

        // Serialize as binary
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xstream.marshal(root, new BinaryStreamWriter(outputStream));

        // Deserialize the binary and check it equals the original object.
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Object binaryResult = xstream.unmarshal(new BinaryStreamReader(inputStream));
        assertObjectsEqual(root, binaryResult);

        return resultRoot;
    }

    protected Object assertWithAsymmetricalXml(Object root, String inXml, String outXml) {
        String resultXml = toXML(root);
        assertEquals(outXml, resultXml);
        Object resultRoot = xstream.fromXML(inXml);
        assertObjectsEqual(root, resultRoot);
        return resultRoot;
    }
    
    /**
     * Allow derived classes to decide how to turn the object into XML text
     */
    protected String toXML(Object root) {
        return xstream.toXML(root);
    }

    /**
     * More descriptive version of assertEquals
     */ 
    protected void assertObjectsEqual(Object expected, Object actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull("Should not be null", actual);
            if (actual.getClass().isArray()) {
                assertArrayEquals(expected, actual);
            } else {
//                assertEquals(expected.getClass(), actual.getClass());
                if (!expected.equals(actual)) {
                    assertEquals("Object deserialization failed",
                            "DESERIALIZED OBJECT\n" + xstream.toXML(expected),
                            "DESERIALIZED OBJECT\n" + xstream.toXML(actual));
                }
            }
        }
    }

    protected void assertArrayEquals(Object expected, Object actual) {
        assertEquals(Array.getLength(expected), Array.getLength(actual));
        for (int i = 0; i < Array.getLength(expected); i++) {
            assertEquals(Array.get(expected, i), Array.get(actual, i));
        }
    }

    protected void assertByteArrayEquals(byte expected[], byte actual[]) {
        assertEquals(dumpBytes(expected), dumpBytes(actual));
    }

    private String dumpBytes(byte bytes[]) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            result.append(bytes[i]).append(' ');
            if (bytes[i] < 100) result.append(' ');
            if (bytes[i] < 10) result.append(' ');
            if (bytes[i] >= 0) result.append(' ');
            if (i % 16 == 15) result.append('\n');
        }
        return result.toString();
    }
}
