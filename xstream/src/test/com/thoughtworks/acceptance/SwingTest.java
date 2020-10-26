/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
