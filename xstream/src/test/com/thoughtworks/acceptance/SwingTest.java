package com.thoughtworks.acceptance;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;

public class SwingTest extends AbstractAcceptanceTest {

    // JTable is one of the nastiest components to serialize. If this works, we're in good shape :)

    public void testJTable() {
        // Note: JTable does not have a sensible .equals() method, so we compare the XML instead.
        JTable original = new JTable();
        String originalXml = xstream.toXML(original);
        
        JTable deserialized = (JTable) xstream.fromXML(originalXml);
        String deserializedXml = xstream.toXML(deserialized);

        assertEquals(originalXml, deserializedXml);
    }

    public void testDefaultListModel() {
        final DefaultListModel original = new DefaultListModel();
        final JList list = new JList();
        list.setModel(original);
        
        String originalXml = xstream.toXML(original);
        
        DefaultListModel deserialized = (DefaultListModel) xstream.fromXML(originalXml);
        String deserializedXml = xstream.toXML(deserialized);
        
        assertEquals(originalXml, deserializedXml);

        list.setModel(deserialized);
    }
    
}
