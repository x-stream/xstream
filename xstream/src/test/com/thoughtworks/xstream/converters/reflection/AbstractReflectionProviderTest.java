package com.thoughtworks.xstream.converters.reflection;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public abstract class AbstractReflectionProviderTest extends MockObjectTestCase {

    protected ReflectionProvider reflectionProvider;

    public abstract ReflectionProvider createReflectionProvider();

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
        private int a;
        private int b = 2;

        public int getParentB() {
            return b;
        }
    }

    public void testVisitsEachFieldInClass() {
        // setup
        Mock mockBlock = new Mock(ReflectionProvider.Block.class);

        // expect
        mockBlock.expect(once())
                .method("visit")
                .with(eq("a"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expect(once())
                .method("visit")
                .with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING);

        // execute
        reflectionProvider.readSerializableFields(new WithFields(), (ReflectionProvider.Block) mockBlock.proxy());

        // verify
        mockBlock.verify();
    }

    public static class SubClassWithFields extends WithFields {
        private int c;
    }

    public void testVisitsEachFieldInHeirarchy() {
        // setup
        Mock mockBlock = new Mock(ReflectionProvider.Block.class);

        // expect
        mockBlock.expect(once())
                .method("visit")
                .with(eq("a"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expect(once())
                .method("visit")
                .with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING);
        mockBlock.expect(once())
                .method("visit")
                .with(eq("c"), eq(int.class), eq(SubClassWithFields.class), ANYTHING);

        // execute
        reflectionProvider.readSerializableFields(new SubClassWithFields(), (ReflectionProvider.Block) mockBlock.proxy());

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
        Mock mockBlock = new Mock(ReflectionProvider.Block.class);

        // expect
        mockBlock.expect(once())
                .method("visit")
                .with(eq("b"), eq(int.class), eq(SubClassWithHiddenFields.class), ANYTHING)
                .id("first");
        mockBlock.expect(once())
                .method("visit")
                .with(eq("b"), eq(int.class), eq(WithFields.class), ANYTHING)
                .after("first");
        mockBlock.expect(once())
                .method("visit")
                .with(eq("a"), ANYTHING, ANYTHING, ANYTHING);

        // execute
        reflectionProvider.readSerializableFields(new SubClassWithHiddenFields(), (ReflectionProvider.Block) mockBlock.proxy());

        // verify
        mockBlock.verify();
    }

    public void testWritesHiddenFields() {
        SubClassWithHiddenFields o = new SubClassWithHiddenFields();
        reflectionProvider.writeField(o, "b", new Integer(10));
        reflectionProvider.writeField(o, "b", new Integer(20), WithFields.class);
        assertEquals(10, o.getChildB());
        assertEquals(20, o.getParentB());
    }

    private void assertCanCreate(Class type) {
        Object result = reflectionProvider.newInstance(type);
        assertEquals(type, result.getClass());
    }

    protected void assertCannotCreate(Class type) {
        try {
            reflectionProvider.newInstance(type);
            fail("Should not have been able to newInstance " + type);
        } catch (ObjectAccessException goodException) {
        }
    }

    public static class PublicStaticInnerClass {
    }

}

class OuterClass {
}

