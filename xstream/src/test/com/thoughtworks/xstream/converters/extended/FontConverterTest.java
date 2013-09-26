/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
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
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.JVM;

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
        // fonts should be serializable also with pure Java
        xstream = new XStream(new PureJavaReflectionProvider());
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
        if (!JVM.is16()) { // it seems even old 1.5 JDKs fail here (Codehaus Bamboo)
            inAttributes.remove(TextAttribute.TRANSFORM);
            outAttributes.remove(TextAttribute.TRANSFORM);
        }

        assertEquals(inAttributes, outAttributes);
    }
    
    public void testUnmarshalsCurrentFormat() {
        // XML representation since 1.4.5
        String xml= (""
                + "<awt-font>\n"
                + "  <posture class='null'/>\n"
                + "  <weight class='float'>2.0</weight>\n"
                + "  <superscript class='null'/>\n"
                + "  <transform class='null'/>\n"
                + "  <size class='float'>20.0</size>\n"
                + "  <width class='null'/>\n"
                + "  <family class='string'>Arial</family>\n"
                + "</awt-font>").replace('\'', '"');
        Font out = (Font) xstream.fromXML(xml);
        
        // assert
        assertEquals(in, out);
    }
    
    public void testUnmarshalsOldFormat() {
        // XML representation pre 1.4.5
        String xml = ""
            + "<awt-font>\n"
            + "  <attributes>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>posture</awt-text-attribute>\n"
            + "      <null/>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>weight</awt-text-attribute>\n"
            + "      <float>2.0</float>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>superscript</awt-text-attribute>\n"
            + "      <null/>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>transform</awt-text-attribute>\n"
            + "      <null/>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>size</awt-text-attribute>\n"
            + "      <float>20.0</float>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>width</awt-text-attribute>\n"
            + "      <null/>\n"
            + "    </entry>\n"
            + "    <entry>\n"
            + "      <awt-text-attribute>family</awt-text-attribute>\n"
            + "      <string>Arial</string>\n"
            + "    </entry>\n"
            + "  </attributes>\n"
            + "</awt-font>";
        Font out = (Font) xstream.fromXML(xml);
        
        // assert
        assertEquals(in, out);
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
