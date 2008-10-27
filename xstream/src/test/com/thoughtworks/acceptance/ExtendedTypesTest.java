/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import org.jdom.Element;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Locale;

public class ExtendedTypesTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testAwtColor() {
        boolean isHeadless = Boolean.valueOf(System.getProperty("java.awt.headless", "false")).booleanValue();
        if (!isHeadless || JVM.is15()) {
            Color color = new Color(0, 10, 20, 30);
    
            String expected = "" +
                    "<awt-color>\n" +
                    "  <red>0</red>\n" +
                    "  <green>10</green>\n" +
                    "  <blue>20</blue>\n" +
                    "  <alpha>30</alpha>\n" +
                    "</awt-color>";
    
            assertBothWays(color, expected);
        }
    }

    public void testSqlTimestamp() {
        assertBothWays(new Timestamp(1234),
                "<sql-timestamp>1969-12-31 19:00:01.234</sql-timestamp>");
    }

    public void testSqlTime() {
        assertBothWays(new Time(14, 7, 33),
                "<sql-time>14:07:33</sql-time>");
    }

    public void testSqlDate() {
        assertBothWays(new Date(78, 7, 25),
                "<sql-date>1978-08-25</sql-date>");
    }

    public void testFile() throws IOException {
        // using temp file to avoid os specific or directory layout issues
        File absFile = File.createTempFile("bleh", ".tmp");
        absFile.deleteOnExit();
        assertTrue(absFile.isAbsolute());
        String expectedXml = "<file>" + absFile.getPath() + "</file>";
        assertFilesBothWay(absFile, expectedXml);

        // test a relative file now
        File relFile = new File("bloh.tmp");
        relFile.deleteOnExit();
        assertFalse(relFile.isAbsolute());
        expectedXml = "<file>" + relFile.getPath() + "</file>";
        assertFilesBothWay(relFile, expectedXml);
    }

    private void assertFilesBothWay(File f, String expectedXml) {
        String resultXml = xstream.toXML(f);
        assertEquals(expectedXml, resultXml);
        Object resultObj = xstream.fromXML(resultXml);
        assertEquals(File.class, resultObj.getClass());
        assertEquals(f, resultObj);
        // now comes the part that fails without a specific converter
        // in the case of a relative file, this will work, because we run the comparison test from the same working directory
        assertEquals(f.getAbsolutePath(), ((File)resultObj).getAbsolutePath());
        assertEquals(f.isAbsolute(), ((File)resultObj).isAbsolute()); // needed because File's equals method only compares the path getAttribute, at least in the win32 implementation
    }

    public void testLocale() {
        assertBothWays(new Locale("zh", "", ""), "<locale>zh</locale>");
        assertBothWays(new Locale("zh", "CN", ""), "<locale>zh_CN</locale>");
    }
    
    public void testCanHandleJDomElement() {
        Element element = new Element("JUnit");

        String expected = "" +
                "<org.jdom.Element serialization=\"custom\">\n" + 
                "  <org.jdom.Element>\n" + 
                "    <default>\n" + 
                "      <attributes>\n" + 
                "        <size>0</size>\n" + 
                "        <parent reference=\"../../../..\"/>\n" + 
                "      </attributes>\n" + 
                "      <content>\n" + 
                "        <size>0</size>\n" + 
                "        <parent class=\"org.jdom.Element\" reference=\"../../../..\"/>\n" + 
                "      </content>\n" + 
                "      <name>JUnit</name>\n" + 
                "    </default>\n" + 
                "    <string></string>\n" + 
                "    <string></string>\n" + 
                "    <byte>0</byte>\n" + 
                "  </org.jdom.Element>\n" + 
                "</org.jdom.Element>";

        assertBothWays(element, expected);
    }
    
}
