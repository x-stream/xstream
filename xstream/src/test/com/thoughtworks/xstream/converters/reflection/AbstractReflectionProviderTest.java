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

package com.thoughtworks.xstream.converters.reflection;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


public abstract class AbstractReflectionProviderTest extends MockObjectTestCase {

    protected ReflectionProvider reflectionProvider;

    public abstract ReflectionProvider createReflectionProvider();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reflectionProvider = createReflectionProvider();
    }

    public void testConstructsStandardClass() {
        assertCanCreate(OuterClass.class);
    }

    public void testConstructsStaticInnerClass() {
        assertCanCreate(PublicStaticInnerClass.class);
    }

    public static class WithFields {
        @SuppressWarnings("unused")
        private int a;
        private int b = 2;

        public int getParentB() {
            return b;
        }
    }

    public void testVisitsEachFieldInClass() {
        // setup
        final Mock mockBlock = new Mock(ReflectionProvider.Visitor.class);

        // expect
        mockBlock.expects(once()).method("visit").with(eq("a"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expects(once()).method("visit").with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING);

        // execute
        reflectionProvider.visitSerializableFields(new WithFields(), (ReflectionProvider.Visitor)mockBlock.proxy());

        // verify
        mockBlock.verify();
    }

    public static class SubClassWithFields extends WithFields {
        @SuppressWarnings("unused")
        private int c;
    }

    public void testVisitsEachFieldInHeirarchy() {
        // setup
        final Mock mockBlock = new Mock(ReflectionProvider.Visitor.class);

        // expect
        mockBlock.expects(once()).method("visit").with(eq("a"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expects(once()).method("visit").with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expects(once()).method("visit").with(eq("c"), eq(int.class), eq(SubClassWithFields.class), ANYTHING);

        // execute
        reflectionProvider.visitSerializableFields(new SubClassWithFields(), (ReflectionProvider.Visitor)mockBlock
            .proxy());

        // verify
        mockBlock.verify();
    }

    public static class SubClassWithHiddenFields extends WithFields {
        private int b = 3;

        public int getChildB() {
            return b;
        }
    }

    public void testVisitsFieldsHiddenBySubclass() {
        // setup
        final Mock mockBlock = new Mock(ReflectionProvider.Visitor.class);

        // expect
        mockBlock.expects(once()).method("visit").with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING).id(
            "first");
        mockBlock
            .expects(once())
            .method("visit")
            .with(eq("b"), eq(int.class), eq(SubClassWithHiddenFields.class), ANYTHING)
            .after("first");
        mockBlock.expects(once()).method("visit").with(eq("a"), ANYTHING, ANYTHING, ANYTHING);

        // execute
        reflectionProvider.visitSerializableFields(new SubClassWithHiddenFields(), (ReflectionProvider.Visitor)mockBlock
            .proxy());

        // verify
        mockBlock.verify();
    }

    public void testWritesHiddenFields() {
        final SubClassWithHiddenFields o = new SubClassWithHiddenFields();
        reflectionProvider.writeField(o, "b", new Integer(10), null);
        reflectionProvider.writeField(o, "b", new Integer(20), WithFields.class);
        assertEquals(10, o.getChildB());
        assertEquals(20, o.getParentB());
    }

    protected void assertCanCreate(final Class<?> type) {
        final Object result = reflectionProvider.newInstance(type);
        assertEquals(type, result.getClass());
    }

    protected void assertCannotCreate(final Class<?> type) {
        try {
            reflectionProvider.newInstance(type);
            fail("Should not have been able to newInstance " + type);
        } catch (final ObjectAccessException goodException) {
        }
    }

    public static class PublicStaticInnerClass {}

}

class OuterClass {}
