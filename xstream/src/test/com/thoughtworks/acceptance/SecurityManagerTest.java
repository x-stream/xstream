/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. March 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.testutil.DynamicSecurityManager;

import junit.framework.TestCase;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.PropertyPermission;


/**
 * Test XStream with an active SecurityManager. Note, that it is intentional, that this test is
 * not derived from AbstractAcceptanceTest to avoid loaded classes before the SecurityManager is
 * in action. Also run each fixture in its own to avoid side-effects.
 * 
 * @author J&ouml;rg Schaible
 */
public class SecurityManagerTest extends TestCase {

    private XStream xstream;
    private DynamicSecurityManager securityManager;
    private CodeSource defaultCodeSource;
    private File mainClasses;
    private File testClasses;
    private File libs;
    private File libsJDK13;

    protected void setUp() throws Exception {
        super.setUp();
        System.setSecurityManager(null);
        defaultCodeSource = new CodeSource(null, (Certificate[])null);
        mainClasses = new File(new File(
                new File(System.getProperty("user.dir"), "target"), "classes"), "-");
        testClasses = new File(new File(
                new File(System.getProperty("user.dir"), "target"), "test-classes"), "-");
        libs = new File(new File(System.getProperty("user.dir"), "lib"), "*");
        if (!JVM.is14()) {
            libsJDK13 = new File(new File(
                    new File(System.getProperty("user.dir"), "lib"), "jdk1.3"), "*");
        }
        securityManager = new DynamicSecurityManager();
        Policy policy = Policy.getPolicy();
        securityManager.setPermissions(defaultCodeSource, policy
                .getPermissions(defaultCodeSource));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "setSecurityManager"));
    }

    protected void tearDown() throws Exception {
        System.setSecurityManager(null);
        super.tearDown();
    }

    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch(Throwable e) {
            for (final Iterator iter = securityManager.getFailedPermissions().iterator(); iter.hasNext();) {
                final Permission permission = (Permission)iter.next();
                System.out.println("SecurityException: Permission " + permission.toString());
            }
            throw e;
        }
    }

    public void testSerializeWithXpp3DriverAndSun14ReflectionProviderAndActiveSecurityManager() {
        if (JVM.is14()) {
            securityManager.addPermission(defaultCodeSource, new FilePermission(mainClasses
                    .toString(), "read"));
            securityManager.addPermission(defaultCodeSource, new FilePermission(testClasses
                    .toString(), "read"));
            securityManager.addPermission(defaultCodeSource, new FilePermission(
                    libs.toString(), "read"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "accessDeclaredMembers"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "accessClassInPackage.sun.reflect"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "accessClassInPackage.sun.misc"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "createClassLoader"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "reflectionFactoryAccess"));
            securityManager.addPermission(defaultCodeSource, new ReflectPermission(
                    "suppressAccessChecks"));
            // permissions necessary for CGLIBMapper
            securityManager.addPermission(defaultCodeSource, new PropertyPermission(
                    "cglib.debugLocation", "read"));
            securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                    "getProtectionDomain"));
            securityManager.setReadOnly();
            System.setSecurityManager(securityManager);

            // uses implicit Sun14ReflectionProvider in JDK >= 1.4, since it has the appropriate
            // rights
            xstream = new XStream();

            assertBothWays();
        }
    }

    public void testSerializeWithXpp3DriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        securityManager.addPermission(defaultCodeSource, new FilePermission(mainClasses
                .toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(testClasses
                .toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                libs.toString(), "read"));
        if (libsJDK13 != null) {
            securityManager.addPermission(defaultCodeSource, new FilePermission(libsJDK13
                    .toString(), "read"));
        }
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessDeclaredMembers"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "createClassLoader"));
        securityManager.addPermission(defaultCodeSource, new ReflectPermission(
                "suppressAccessChecks"));
        // permissions necessary for CGLIBMapper
        securityManager.addPermission(defaultCodeSource, new PropertyPermission(
                "cglib.debugLocation", "read"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "getProtectionDomain"));
        securityManager.setReadOnly();
        System.setSecurityManager(securityManager);

        xstream = new XStream(new PureJavaReflectionProvider());

        assertBothWays();
    }

    public void testSerializeWithDomDriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        securityManager.addPermission(defaultCodeSource, new FilePermission(mainClasses
                .toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(testClasses
                .toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                libs.toString(), "read"));
        if (libsJDK13 != null) {
            securityManager.addPermission(defaultCodeSource, new FilePermission(libsJDK13
                    .toString(), "read"));
        }
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessDeclaredMembers"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "createClassLoader"));
        securityManager.addPermission(defaultCodeSource, new ReflectPermission(
                "suppressAccessChecks"));
        // permissions necessary for CGLIBMapper
        securityManager.addPermission(defaultCodeSource, new PropertyPermission(
                "cglib.debugLocation", "read"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "getProtectionDomain"));
        securityManager.setReadOnly();
        System.setSecurityManager(securityManager);

        // uses implicit PureJavaReflectionProvider, since Sun14ReflectionProvider cannot be
        // loaded
        xstream = new XStream(new DomDriver());

        assertBothWays();
    }

    private void assertBothWays() {

        xstream.alias("software", Software.class);

        final Software sw = new Software("jw", "xstr");
        final String xml = "<software>\n"
                + "  <vendor>jw</vendor>\n"
                + "  <name>xstr</name>\n"
                + "</software>";

        String resultXml = xstream.toXML(sw);
        assertEquals(xml, resultXml);
        Object resultRoot = xstream.fromXML(resultXml);
        if (!sw.equals(resultRoot)) {
            assertEquals("Object deserialization failed", "DESERIALIZED OBJECT\n"
                    + xstream.toXML(sw), "DESERIALIZED OBJECT\n" + xstream.toXML(resultRoot));
        }
    }
}
