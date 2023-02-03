/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 18. November 2022 by Joerg Schaible
 */
package com.thoughtworks.xstream.security;

import java.io.Serializable;

import com.thoughtworks.acceptance.AliasTest;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


/**
 * Tests the {@link WildcardTypePermission}.
 *
 * @author J&ouml;rg Schaible
 */
public class WildcardTypePermissionTest extends TestCase {

    private static class ATeam {}

    private static class BTeam {}

    private static class Cteam {
        class Inner {}
    }

    public void testSingleCharacterPattern() {
        final TypePermission permission = new WildcardTypePermission(WildcardTypePermissionTest.class.getName()
            + "$?Team");
        assertTrue("Permission denied " + ATeam.class.getName(), permission.allows(ATeam.class));
        assertTrue("Permission denied " + BTeam.class.getName(), permission.allows(BTeam.class));
        assertFalse("Permission allowed " + Cteam.class.getName(), permission.allows(Cteam.class));
    }

    public void testSinglePackagePattern() {
        final TypePermission permission = new WildcardTypePermission("com.thoughtworks.acceptance.*");
        assertTrue("Permission denied " + AliasTest.class.getName(), permission.allows(AliasTest.class));
        assertFalse("Permission allowed " + Software.class.getName(), permission.allows(Software.class));
    }

    public void testSinglePackagePatternInBetween() {
        final TypePermission permission = new WildcardTypePermission("com.*.acceptance.*");
        assertTrue("Permission denied " + AliasTest.class.getName(), permission.allows(AliasTest.class));
        assertFalse("Permission allowed " + Software.class.getName(), permission.allows(Software.class));
    }

    public void testMultiplePackagePattern() {
        final TypePermission permission = new WildcardTypePermission("com.thoughtworks.acceptance.**");
        assertTrue("Permission denied " + AliasTest.class.getName(), permission.allows(AliasTest.class));
        assertTrue("Permission denied " + Software.class.getName(), permission.allows(Software.class));
        assertFalse("Permission allowed " + XStream.class.getName(), permission.allows(XStream.class));
    }

    public void testSinglePackagePatternExcludesAnonymousTypes() {
        @SuppressWarnings("serial")
        final Serializable s = new Serializable() {};
        @SuppressWarnings("serial")
        class S implements Serializable {}
        final TypePermission permission = new WildcardTypePermission("com.thoughtworks.xstream.security.*");
        assertTrue("Permission denied " + ATeam.class.getName(), permission.allows(ATeam.class));
        assertTrue("Permission denied " + Cteam.Inner.class.getName(), permission.allows(Cteam.Inner.class));
        assertTrue("Permission denied " + WildcardTypePermissionTest.class.getName(), permission.allows(
            WildcardTypePermissionTest.class));
        assertFalse("Permission allowed " + s.getClass().getName(), permission.allows(s.getClass()));
        assertFalse("Permission allowed " + S.class.getName(), permission.allows(S.class));
    }

    public void testMultiplePackagePatternExcludesAnonymousTypes() {
        @SuppressWarnings("serial")
        final Serializable s = new Serializable() {};
        @SuppressWarnings("serial")
        class S implements Serializable {}
        final TypePermission permission = new WildcardTypePermission("com.thoughtworks.xstream.**");
        assertTrue("Permission denied " + ATeam.class.getName(), permission.allows(ATeam.class));
        assertTrue("Permission denied " + Cteam.Inner.class.getName(), permission.allows(Cteam.Inner.class));
        assertTrue("Permission denied " + WildcardTypePermissionTest.class.getName(), permission.allows(
            WildcardTypePermissionTest.class));
        assertFalse("Permission allowed " + s.getClass().getName(), permission.allows(s.getClass()));
        assertFalse("Permission allowed " + S.class.getName(), permission.allows(S.class));
    }

    public void testSinglePackagePatternExplicitlyIncludeAnonymousTypes() {
        @SuppressWarnings("serial")
        final Serializable s = new Serializable() {};
        @SuppressWarnings("serial")
        class S implements Serializable {}
        final TypePermission permission = new WildcardTypePermission(true, "com.thoughtworks.xstream.security.*");
        assertTrue("Permission denied " + ATeam.class.getName(), permission.allows(ATeam.class));
        assertTrue("Permission denied " + Cteam.Inner.class.getName(), permission.allows(Cteam.Inner.class));
        assertTrue("Permission denied " + WildcardTypePermissionTest.class.getName(), permission.allows(
            WildcardTypePermissionTest.class));
        assertTrue("Permission denied " + s.getClass().getName(), permission.allows(s.getClass()));
        assertTrue("Permission denied " + S.class.getName(), permission.allows(S.class));
    }

    public void testMultiplePackagePatternExplicitlyIncludeAnonymousTypes() {
        @SuppressWarnings("serial")
        final Serializable s = new Serializable() {};
        @SuppressWarnings("serial")
        class S implements Serializable {}
        final TypePermission permission = new WildcardTypePermission(true, "com.thoughtworks.xstream.**");
        assertTrue("Permission denied " + ATeam.class.getName(), permission.allows(ATeam.class));
        assertTrue("Permission denied " + Cteam.Inner.class.getName(), permission.allows(Cteam.Inner.class));
        assertTrue("Permission denied " + WildcardTypePermissionTest.class.getName(), permission.allows(
            WildcardTypePermissionTest.class));
        assertTrue("Permission denied " + s.getClass().getName(), permission.allows(s.getClass()));
        assertTrue("Permission denied " + S.class.getName(), permission.allows(S.class));
    }
}
