/*
 * Copyright (C) 2013, 2014, 2017, 2018, 2020, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 23. December 2013 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.beans.EventHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.ProxyTypePermission;


/**
 * @author J&ouml;rg Schaible
 */
public class SecurityVulnerabilityTest extends AbstractAcceptanceTest {

    private final static StringBuffer BUFFER = new StringBuffer();

    protected void setUp() throws Exception {
        super.setUp();
        BUFFER.setLength(0);
        xstream.alias("runnable", Runnable.class);
        xstream.allowTypeHierarchy(Runnable.class);
        xstream.addPermission(ProxyTypePermission.PROXIES);
    }

    public void testCannotInjectEventHandler() {
        final String xml = ""
            + "<string class='runnable-array'>\n"
            + "  <dynamic-proxy>\n"
            + "    <interface>java.lang.Runnable</interface>\n"
            + "    <handler class='java.beans.EventHandler'>\n"
            + "      <target class='com.thoughtworks.acceptance.SecurityVulnerabilityTest$Exec'/>\n"
            + "      <action>exec</action>\n"
            + "    </handler>\n"
            + "  </dynamic-proxy>\n"
            + "</string>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            assertTrue(e.getMessage().indexOf(EventHandler.class.getName()) > 0);
        }
        assertEquals(0, BUFFER.length());
    }

    public void testExplicitlyConvertEventHandler() {
        final String xml = ""
            + "<string class='runnable-array'>\n"
            + "  <dynamic-proxy>\n"
            + "    <interface>java.lang.Runnable</interface>\n"
            + "    <handler class='java.beans.EventHandler'>\n"
            + "      <target class='com.thoughtworks.acceptance.SecurityVulnerabilityTest$Exec'/>\n"
            + "      <action>exec</action>\n"
            + "    </handler>\n"
            + "  </dynamic-proxy>\n"
            + "</string>";

        xstream.allowTypes(new Class[]{EventHandler.class});

        final Runnable[] array = (Runnable[])xstream.fromXML(xml);
        assertEquals(0, BUFFER.length());
        array[0].run();
        assertEquals("Executed!", BUFFER.toString());
    }

    public void testExplicitlyConvertImageIOContainsFilter() {
        if (JVM.isVersion(7)) {
            final String xml = ""
                + "<string class='javax.imageio.spi.FilterIterator'>\n"
                + " <iter class='java.util.ArrayList$Itr'>\n"
                + "   <cursor>0</cursor>\n"
                + "   <lastRet>1</lastRet>\n"
                + "   <expectedModCount>1</expectedModCount>\n"
                + "   <outer-class>\n"
                + "     <com.thoughtworks.acceptance.SecurityVulnerabilityTest_-Exec/>\n"
                + "   </outer-class>\n"
                + " </iter>\n"
                + " <filter class='javax.imageio.ImageIO$ContainsFilter'>\n"
                + "   <method>\n"
                + "     <class>com.thoughtworks.acceptance.SecurityVulnerabilityTest$Exec</class>\n"
                + "     <name>exec</name>\n"
                + "     <parameter-types/>\n"
                + "   </method>\n"
                + "   <name>exec</name>\n"
                + " </filter>\n"
                + " <next/>\n"
                + "</string>";

            xstream.allowTypes(new String[]{"javax.imageio.ImageIO$ContainsFilter"});
            xstream.allowTypeHierarchy(Iterator.class);

            final Iterator iterator = (Iterator)xstream.fromXML(xml);
            assertEquals(0, BUFFER.length());
            iterator.next();
            assertEquals("Executed!", BUFFER.toString());
        }
    }

    public static class Exec {

        public void exec() {
            BUFFER.append("Executed!");
        }
    }

    public void testInstanceOfVoid() {
        try {
            xstream.fromXML("<void/>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            // OK
        }
    }

    public void testDeniedInstanceOfVoid() {
        xstream.addPermission(AnyTypePermission.ANY); // clear out defaults
        xstream.denyTypes(new Class[]{void.class, Void.class});
        try {
            xstream.fromXML("<void/>");
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            // OK
        }
    }

    public void testAllowedInstanceOfVoid() {
        xstream.allowTypes(new Class[]{void.class, Void.class});
        try {
            xstream.fromXML("<void/>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals("void", e.get("construction-type"));
        }
    }

    public void testCannotUseJaxwsInputStreamToDeleteFile() {
        if (JVM.isVersion(5)) {
            final String xml = ""
                + "<is class='com.sun.xml.ws.util.ReadAllStream$FileStream'>\n"
                + "  <tempFile>target/junit/test.txt</tempFile>\n"
                + "</is>";

            xstream.aliasType("is", InputStream.class);
            try {
                xstream.fromXML(xml);
                fail("Thrown " + ConversionException.class.getName() + " expected");
            } catch (final ForbiddenClassException e) {
                // OK
            }
        }
    }

    public void testExplicitlyUseJaxwsInputStreamToDeleteFile() throws IOException {
        if (JVM.isVersion(5)) {
            final File testDir = new File("target/junit");
            final File testFile = new File(testDir, "test.txt");
            try {
                testDir.mkdirs();

                final OutputStream out = new FileOutputStream(testFile);
                out.write("JUnit".getBytes());
                out.flush();
                out.close();

                assertTrue("Test file " + testFile.getPath() + " does not exist.", testFile.exists());

                final String xml = ""
                    + "<is class='com.sun.xml.ws.util.ReadAllStream$FileStream'>\n"
                    + "  <tempFile>target/junit/test.txt</tempFile>\n"
                    + "</is>";

                xstream.addPermission(AnyTypePermission.ANY); // clear out defaults
                xstream.aliasType("is", InputStream.class);

                InputStream is = null;
                try {
                    is = (InputStream)xstream.fromXML(xml);
                } catch (final ForbiddenClassException e) {
                    // OK
                }

                assertTrue("Test file " + testFile.getPath() + " no longer exists.", testFile.exists());

                byte[] data = new byte[10];
                is.read(data);
                is.close();

                assertFalse("Test file " + testFile.getPath() + " still exists exist.", testFile.exists());
            } finally {
                if (testFile.exists()) {
                    testFile.delete();
                }
                if (testDir.exists()) {
                    testDir.delete();
                }
            }
        }
    }

    public void testCannotInjectManipulatedByteArryInputStream() {
        xstream.alias("bais", ByteArrayInputStream.class);
        final String xml = ""
            + "<bais>\n"
            + "  <buf></buf>\n"
            + "  <pos>-2147483648</pos>\n"
            + "  <mark>0</mark>\n"
            + "  <count>0</count>\n"
            + "</bais>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(e.getMessage(),ByteArrayInputStream.class.getName());
        }
    }

    public void testExplicitlyUnmarshalEndlessByteArryInputStream() {
        xstream.alias("bais", ByteArrayInputStream.class);
        xstream.allowTypes(new Class[]{ByteArrayInputStream.class});

        final String xml = ""
            + "<bais>\n"
            + "  <buf></buf>\n"
            + "  <pos>-2147483648</pos>\n"
            + "  <mark>0</mark>\n"
            + "  <count>0</count>\n"
            + "</bais>";
        
        final byte[] data = new byte[10];
        final ByteArrayInputStream bais = (ByteArrayInputStream)xstream.fromXML(xml);
        int i = 5;
        try {
            while(bais.read(data, 0, 10) == 0) {
                if (--i == 0) {
                    break;
                }
            }
            assertEquals("Unlimited reads of ByteArrayInputStream returning 0 bytes expected", 0, i);
        } catch(ArrayIndexOutOfBoundsException e) {
            assertEquals("ArrayIndexOutOfBoundsException expected reading invalid stream", 5, i);
        }
    }
}
