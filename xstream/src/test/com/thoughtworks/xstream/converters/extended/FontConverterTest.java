/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.util.Map;

public class FontConverterTest extends TestCase {
    private XStream xstream;
    private Font in;

    public static Test suite() {
        // Only try to run this test case if graphics environment is available
        try {
            new Font("Arial", Font.BOLD, 20);
            return new TestSuite(FontConverterTest.class);
        } catch (Throwable t) {
            return new TestSuite();
        } 
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        in = new Font("Arial", Font.BOLD, 20);
    }

    public void testConvertsToFontThatEqualsOriginal() {
        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        // assert
        assertEquals(in, out);
    }

    public void testProducesFontThatHasTheSameAttributes() {
        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        // assert
        Map inAttributes = in.getAttributes();
        Map outAttributes = out.getAttributes();

        // these attributes don't have a valid .equals() method (bad Sun!), so we can't use them in the test.
        inAttributes.remove(TextAttribute.TRANSFORM);
        outAttributes.remove(TextAttribute.TRANSFORM);

        assertEquals(inAttributes, outAttributes);
    }

    public void testCorrectlyInitializesFontToPreventJvmCrash() {
        // If a font has not been constructed in the correct way, the JVM crashes horribly through some internal
        // native code, whenever the font is rendered to screen.

        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        Toolkit.getDefaultToolkit().getFontMetrics(out);
        // if the JVM hasn't crashed yet, we're good.

    }
}
