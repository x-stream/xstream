/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2014, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. April 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.thoughtworks.xstream.XStream;


public class SwingTest extends AbstractAcceptanceTest {

    @Override
    protected void setupSecurity(final XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypesByWildcard("javax.swing.**", "java.awt.**", "java.beans.**", "sun.swing.**");
    }

    // JTable is one of the nastiest components to serialize. If this works, we're in good shape :)
    public void testJTable() {
        // Note: JTable does not have a sensible .equals() method, so we compare the XML instead.
        final JTable original = new JTable();
        final String originalXml = xstream.toXML(original);

        final JTable deserialized = (JTable)xstream.fromXML(originalXml);
        final String deserializedXml = xstream.toXML(deserialized);

        assertEquals(originalXml, deserializedXml);
    }

    public void testDefaultListModel() {
        final DefaultListModel<Object> original = new DefaultListModel<>();
        final JList<Object> list = new JList<>();
        list.setModel(original);

        final String originalXml = xstream.toXML(original);

        final DefaultListModel<Object> deserialized = xstream.fromXML(originalXml);
        final String deserializedXml = xstream.toXML(deserialized);

        assertEquals(originalXml, deserializedXml);

        list.setModel(deserialized);
    }

    public void testMetalLookAndFeel() {
        final LookAndFeel plaf = new MetalLookAndFeel();
        final String originalXml = xstream.toXML(plaf);
        assertBothWays(plaf, originalXml);
    }
}
