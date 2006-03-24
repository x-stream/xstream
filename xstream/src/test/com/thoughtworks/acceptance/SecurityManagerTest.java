package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.testutil.DynamicSecurityManager;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.security.CodeSource;
import java.security.Policy;


public class SecurityManagerTest extends AbstractAcceptanceTest {

    private DynamicSecurityManager securityManager;
    private CodeSource defaultCodeSource;
    private File classes;
    private File testClasses;
    private File libs;

    protected void setUp() throws Exception {
        super.setUp();
        System.setSecurityManager(null);
        defaultCodeSource = new CodeSource(null, null);
        classes = new File(
                new File(new File(System.getProperty("user.dir"), "target"), "classes"), "-");
        testClasses = new File(new File(
                new File(System.getProperty("user.dir"), "target"), "test-classes"), "-");
        libs = new File(new File(System.getProperty("user.dir"), "lib"), "*");
        securityManager = new DynamicSecurityManager();
        Policy policy = Policy.getPolicy();
        securityManager.setPermissions(defaultCodeSource, policy.getPermissions(defaultCodeSource));
        securityManager.addPermission(
                defaultCodeSource, new RuntimePermission("setSecurityManager"));
    }

    protected void tearDown() throws Exception {
        System.setSecurityManager(null);
        super.tearDown();
    }

    public void testSerializeWithXpp3DriverAndActiveSecurityManager() {
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                classes.toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                testClasses.toString(), "read"));
        securityManager.addPermission(
                defaultCodeSource, new FilePermission(libs.toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessDeclaredMembers"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessClassInPackage.sun.reflect"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessClassInPackage.sun.misc"));
        securityManager
                .addPermission(defaultCodeSource, new RuntimePermission("createClassLoader"));
        securityManager.addPermission(defaultCodeSource, new ReflectPermission(
                "suppressAccessChecks"));
        securityManager.setReadOnly();
        System.setSecurityManager(securityManager);

        assertBothWays();
    }

    public void testSerializeWithDomDriverAndActiveSecurityManager() {
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                classes.toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new FilePermission(
                testClasses.toString(), "read"));
        securityManager.addPermission(defaultCodeSource, new RuntimePermission(
                "accessDeclaredMembers"));
        securityManager
                .addPermission(defaultCodeSource, new RuntimePermission("createClassLoader"));
        securityManager.addPermission(defaultCodeSource, new ReflectPermission(
                "suppressAccessChecks"));
        securityManager.setReadOnly();
        System.setSecurityManager(securityManager);

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
        assertBothWays(sw, xml);
    }
}
