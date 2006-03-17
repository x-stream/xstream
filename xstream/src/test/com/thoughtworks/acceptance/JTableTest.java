package com.thoughtworks.acceptance;

import javax.swing.JTable;

public class JTableTest extends AbstractAcceptanceTest {

    // JTable is one of the nastiest components to serialize. If this works, we're in good shape :)

    public void test$$$$TODO$$$$JTable() {
        // Note: JTable does not have a sensible .equals() method, so we compare the XML instead.

        if (false) {
        JTable original = new JTable();
        String originalXml = xstream.toXML(original);

        JTable deserialized = (JTable) xstream.fromXML(originalXml);
        String deserializedXml = xstream.toXML(deserialized);

        assertEquals(originalXml, deserializedXml);
        }
    }

}
