/*
 * Copyright (C) 2006, 2007, 2009, 2010 XStream Committers.
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
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.testutil.DynamicSecurityManager;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.NetPermission;
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
    private DynamicSecurityManager sm;
    private CodeSource source;

    protected void setUp() throws Exception {
        super.setUp();
        System.setSecurityManager(null);
        source = new CodeSource(new File("target").toURI().toURL(), (Certificate[])null);

        sm = new DynamicSecurityManager();
        Policy policy = Policy.getPolicy();
        sm.setPermissions(source, policy.getPermissions(source));
        sm.addPermission(source, new RuntimePermission("setSecurityManager"));
        
        File mainClasses = new File(System.getProperty("user.dir"), "target/classes/-");
        File testClasses = new File(System.getProperty("user.dir"), "target/test-classes/-");
        String[] javaClassPath = StringUtils.split(System.getProperty("java.class.path"), File.pathSeparatorChar);
        File javaHome = new File(System.getProperty("java.home"), "-");
        
        // necessary permission start here
        sm.addPermission(source, new FilePermission(mainClasses.toString(), "read"));
        sm.addPermission(source, new FilePermission(testClasses.toString(), "read"));
        sm.addPermission(source, new FilePermission(javaHome.toString(), "read"));
        for (int i = 0; i < javaClassPath.length; ++i) {
            if (javaClassPath[i].endsWith(".jar")) {
                sm.addPermission(source, new FilePermission(javaClassPath[i], "read"));
            }
        }
    }

    protected void tearDown() throws Exception {
        System.setSecurityManager(null);
        super.tearDown();
    }

    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch(Throwable e) {
            for (final Iterator iter = sm.getFailedPermissions().iterator(); iter.hasNext();) {
                final Permission permission = (Permission)iter.next();
                System.out.println("SecurityException: Permission " + permission.toString());
            }
            throw e;
        }
    }

    public void testSerializeWithXppDriverAndSun14ReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.reflect"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.misc"));
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new RuntimePermission("reflectionFactoryAccess"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        // uses implicit Sun14ReflectionProvider in JDK >= 1.4, since it has the appropriate
        // rights
        xstream = new XStream();

        assertBothWays();
    }

    public void testSerializeWithXppDriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new PureJavaReflectionProvider());

        assertBothWays();
    }

    public void testSerializeWithDomDriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new RuntimePermission("reflectionFactoryAccess"));
        sm.addPermission(source, new PropertyPermission("com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration", "read"));
        sm.addPermission(source, new PropertyPermission("elementAttributeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("entityExpansionLimit", "read"));
        sm.addPermission(source, new PropertyPermission("http://java.sun.com/xml/dom/properties/ancestor-check", "read"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.parsers.DocumentBuilderFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("maxOccurLimit", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new PureJavaReflectionProvider(), new DomDriver());

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
