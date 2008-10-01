/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. April 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.core.JVM;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class SwingTest extends AbstractAcceptanceTest {

    // JTable is one of the nastiest components to serialize. If this works, we're in good shape :)

    public void testJTable() {
        boolean isHeadless = Boolean.valueOf(System.getProperty("java.awt.headless", "false")).booleanValue();
        if (!isHeadless || JVM.is15()) {
            // Note: JTable does not have a sensible .equals() method, so we compare the XML instead.
            JTable original = new JTable();
            String originalXml = xstream.toXML(original);
            
            JTable deserialized = (JTable) xstream.fromXML(originalXml);
            String deserializedXml = xstream.toXML(deserialized);
    
            assertEquals(originalXml, deserializedXml);
        }
    }

    public void testDefaultListModel() {
        boolean isHeadless = Boolean.valueOf(System.getProperty("java.awt.headless", "false")).booleanValue();
        if (!isHeadless || JVM.is15()) {
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
    
    public void testMetalLookAndFeel() {
        LookAndFeel plaf = new MetalLookAndFeel();
        String originalXml = xstream.toXML(plaf);
        assertBothWays(plaf, originalXml);
    }
}
