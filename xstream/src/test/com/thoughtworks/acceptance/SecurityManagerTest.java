/*
 * Copyright (C) 2006, 2007, 2009, 2010, 2013, 2014, 2015, 2016, 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. March 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.io.File;
import java.io.FilePermission;
import java.io.SerializablePermission;
import java.lang.reflect.ReflectPermission;
import java.net.NetPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.PropertyPermission;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.SimpleStaxDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.testutil.DynamicSecurityManager;

import junit.framework.TestCase;


/**
 * Test XStream with an active SecurityManager. Note, that it is intentional, that this test is not derived from
 * AbstractAcceptanceTest to avoid loaded classes before the SecurityManager is in action. Also run each fixture in its
 * own to avoid side-effects.
 *
 * @author J&ouml;rg Schaible
 */
public class SecurityManagerTest extends TestCase {

    private XStream xstream;
    private DynamicSecurityManager sm;
    private CodeSource source;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setSecurityManager(null);
        source = new CodeSource(new File("target").toURI().toURL(), (Certificate[])null);

        sm = new DynamicSecurityManager();
        final Policy policy = Policy.getPolicy();
        sm.setPermissions(source, policy.getPermissions(source));
        sm.addPermission(source, new RuntimePermission("setSecurityManager"));

        final File mainClasses = new File(System.getProperty("user.dir"), "target/classes/-");
        final File testClasses = new File(System.getProperty("user.dir"), "target/test-classes/-");
        final String[] javaClassPath = StringUtils.split(System.getProperty("java.class.path"), File.pathSeparatorChar);
        final File javaHome = new File(System.getProperty("java.home"), "-");

        // necessary permission start here
        sm.addPermission(source, new FilePermission(mainClasses.toString(), "read"));
        sm.addPermission(source, new FilePermission(testClasses.toString(), "read"));
        sm.addPermission(source, new FilePermission(javaHome.toString(), "read"));
        for (final String element : javaClassPath) {
            if (element.endsWith(".jar")) {
                sm.addPermission(source, new FilePermission(element, "read"));
            } else {
                sm.addPermission(source, new FilePermission(element + "/META-INF/services/java.time.chrono.AbstractChronology", "read"));
                sm.addPermission(source, new FilePermission(element + "/META-INF/services/java.time.chrono.Chronology", "read"));
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        System.setSecurityManager(null);
        super.tearDown();
    }

    @Override
    protected void runTest() throws Throwable {
        try {
            super.runTest();
        } catch (final Throwable e) {
            for (final Permission permission : sm.getFailedPermissions()) {
                System.out.println("SecurityException: Permission " + permission.toString());
            }
            throw e;
        }
    }

    public void testSerializeWithSimpleStaxDriverAndSunUnsafeReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.com.sun.xml.internal.stream"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.misc"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.util.resources"));
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("fileSystemProvider"));
        sm.addPermission(source, new RuntimePermission("getClassLoader"));
        sm.addPermission(source, new RuntimePermission("getProtectionDomain"));
        sm.addPermission(source, new RuntimePermission("loadLibrary.nio"));
        sm.addPermission(source, new PropertyPermission("elementAttributeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("entityExpansionLimit", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.locale.providers", "read"));
        sm.addPermission(source, new PropertyPermission("java.nio.file.spi.DefaultFileSystemProvider", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.Arrays.useLegacyMergeSort", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.currency.data", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.accessExternalDTD", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.accessExternalSchema", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.elementAttributeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.entityExpansionLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.entityReplacementLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxElementDepth", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxGeneralEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxOccurLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxParameterEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxXMLNameLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.totalEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("maxOccurLimit", "read"));
        sm.addPermission(source, new PropertyPermission("sun.io.serialization.extendedDebugInfo", "read"));
        sm.addPermission(source, new PropertyPermission("sun.jnu.encoding", "read"));
        sm.addPermission(source, new PropertyPermission("sun.nio.fs.chdirAllowed", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new PropertyPermission("user.dir", "read"));
        sm.addPermission(source, new PropertyPermission("user.timezone", "read,write"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.addPermission(source, new SerializablePermission("enableSubclassImplementation"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new SimpleStaxDriver());

        assertBothWays();
    }

    public void testSerializeWithXppDriverAndSunUnsafeReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.reflect"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.misc"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.text.resources"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.util.resources"));
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("fileSystemProvider"));
        sm.addPermission(source, new RuntimePermission("getClassLoader"));
        sm.addPermission(source, new RuntimePermission("getProtectionDomain"));
        sm.addPermission(source, new RuntimePermission("loadLibrary.nio"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new RuntimePermission("reflectionFactoryAccess"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.locale.providers", "read"));
        sm.addPermission(source, new PropertyPermission("java.nio.file.spi.DefaultFileSystemProvider", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.Arrays.useLegacyMergeSort", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.currency.data", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.util.TimeZone.allowSetDefault", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.io.serialization.extendedDebugInfo", "read"));
        sm.addPermission(source, new PropertyPermission("sun.jnu.encoding", "read"));
        sm.addPermission(source, new PropertyPermission("sun.nio.fs.chdirAllowed", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new PropertyPermission("user.country", "read"));
        sm.addPermission(source, new PropertyPermission("user.dir", "read"));
        sm.addPermission(source, new PropertyPermission("user.timezone", "read,write"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.addPermission(source, new SerializablePermission("enableSubclassImplementation"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new Xpp3Driver());

        assertBothWays();
    }

    public void testSerializeWithXppDriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.misc"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.text.resources"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.util.resources"));
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("fileSystemProvider"));
        sm.addPermission(source, new RuntimePermission("getClassLoader"));
        sm.addPermission(source, new RuntimePermission("getProtectionDomain"));
        sm.addPermission(source, new RuntimePermission("loadLibrary.nio"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.locale.providers", "read"));
        sm.addPermission(source, new PropertyPermission("java.nio.file.spi.DefaultFileSystemProvider", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.Arrays.useLegacyMergeSort", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.currency.data", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.util.TimeZone.allowSetDefault", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.io.serialization.extendedDebugInfo", "read"));
        sm.addPermission(source, new PropertyPermission("sun.jnu.encoding", "read"));
        sm.addPermission(source, new PropertyPermission("sun.nio.fs.chdirAllowed", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new PropertyPermission("user.country", "read"));
        sm.addPermission(source, new PropertyPermission("user.dir", "read"));
        sm.addPermission(source, new PropertyPermission("user.timezone", "read,write"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.addPermission(source, new SerializablePermission("enableSubclassImplementation"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new PureJavaReflectionProvider(), new Xpp3Driver());

        assertBothWays();
    }

    public void testSerializeWithDomDriverAndPureJavaReflectionProviderAndActiveSecurityManager() {
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.misc"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.text.resources"));
        sm.addPermission(source, new RuntimePermission("accessClassInPackage.sun.util.resources"));
        sm.addPermission(source, new RuntimePermission("accessDeclaredMembers"));
        sm.addPermission(source, new RuntimePermission("createClassLoader"));
        sm.addPermission(source, new RuntimePermission("fileSystemProvider"));
        sm.addPermission(source, new RuntimePermission("getClassLoader"));
        sm.addPermission(source, new RuntimePermission("getProtectionDomain"));
        sm.addPermission(source, new RuntimePermission("loadLibrary.nio"));
        sm.addPermission(source, new RuntimePermission("modifyThreadGroup"));
        sm.addPermission(source, new RuntimePermission("reflectionFactoryAccess"));
        sm
            .addPermission(source, new PropertyPermission(
                "com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration", "read"));
        sm.addPermission(source, new PropertyPermission("elementAttributeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("entityExpansionLimit", "read"));
        sm
            .addPermission(source, new PropertyPermission("http://java.sun.com/xml/dom/properties/ancestor-check",
                "read"));
        sm.addPermission(source, new PropertyPermission("ibm.dst.compatibility", "read"));
        sm.addPermission(source, new PropertyPermission("java.home", "read"));
        sm.addPermission(source, new PropertyPermission("java.locale.providers", "read"));
        sm.addPermission(source, new PropertyPermission("java.nio.file.spi.DefaultFileSystemProvider", "read"));
        sm.addPermission(source, new PropertyPermission("java.security.debug", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.Arrays.useLegacyMergeSort", "read"));
        sm.addPermission(source, new PropertyPermission("java.util.currency.data", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.datatype.DatatypeFactory", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.parsers.DocumentBuilderFactory", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.parsers.SAXParserFactory", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.accessExternalDTD", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.accessExternalSchema", "read"));
        sm.addPermission(source, new PropertyPermission("javax.xml.useCatalog", "read"));
        sm.addPermission(source, new PropertyPermission("jaxp.debug", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.util.TimeZone.allowSetDefault", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.cdataChunkSize", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.elementAttributeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.entityExpansionLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.entityReplacementLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxElementDepth", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxGeneralEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxParameterEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxOccurLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.maxXMLNameLimit", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.overrideDefaultParser", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.resetSymbolTable", "read"));
        sm.addPermission(source, new PropertyPermission("jdk.xml.totalEntitySizeLimit", "read"));
        sm.addPermission(source, new PropertyPermission("maxOccurLimit", "read"));
        sm.addPermission(source, new PropertyPermission("sun.boot.class.path", "read"));
        sm.addPermission(source, new PropertyPermission("sun.io.serialization.extendedDebugInfo", "read"));
        sm.addPermission(source, new PropertyPermission("sun.jnu.encoding", "read"));
        sm.addPermission(source, new PropertyPermission("sun.nio.fs.chdirAllowed", "read"));
        sm.addPermission(source, new PropertyPermission("sun.timezone.ids.oldmapping", "read"));
        sm.addPermission(source, new PropertyPermission("user.country", "read"));
        sm.addPermission(source, new PropertyPermission("user.dir", "read"));
        sm.addPermission(source, new PropertyPermission("user.timezone", "read,write"));
        sm.addPermission(source, new NetPermission("specifyStreamHandler"));
        sm.addPermission(source, new ReflectPermission("suppressAccessChecks"));
        sm.addPermission(source, new SerializablePermission("enableSubclassImplementation"));
        sm.setReadOnly();
        System.setSecurityManager(sm);

        xstream = new XStream(new PureJavaReflectionProvider(), new DomDriver());

        assertBothWays();
    }

    private void assertBothWays() {
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("software", Software.class);

        final Software sw = new Software("jw", "xstr");
        final String xml = ""//
            + "<software>\n"
            + "  <vendor>jw</vendor>\n"
            + "  <name>xstr</name>\n"
            + "</software>";

        final String resultXml = xstream.toXML(sw);
        assertEquals(xml, resultXml);
        final Object resultRoot = xstream.fromXML(resultXml);
        if (!sw.equals(resultRoot)) {
            assertEquals("Object deserialization failed", "DESERIALIZED OBJECT\n" + xstream.toXML(sw),
                "DESERIALIZED OBJECT\n" + xstream.toXML(resultRoot));
        }
    }
}
