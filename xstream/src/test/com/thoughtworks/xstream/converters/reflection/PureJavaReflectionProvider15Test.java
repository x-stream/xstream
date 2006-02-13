package com.thoughtworks.xstream.converters.reflection;


public class PureJavaReflectionProvider15Test extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }


    // ---------------------------------------------------------


    public static class WithFinalField {
        private final String s;
        private WithFinalField() {
            this.s = "";
        }
        String getFinal() {
            return s;
        }
    }

    public void testCanCreateWithFinalFIeld() {
        assertCanCreate(WithFinalField.class);
    }

    public void testWriteToFinalField() {
        Object result = reflectionProvider.newInstance(WithFinalField.class);
        reflectionProvider.writeField(result, "s", "foo", WithFinalField.class);
        WithFinalField withFinalField = (WithFinalField)result;
        assertEquals("foo", withFinalField.getFinal());
    }

}

