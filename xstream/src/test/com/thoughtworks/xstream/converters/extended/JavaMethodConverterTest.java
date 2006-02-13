package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class JavaMethodConverterTest extends AbstractAcceptanceTest {

    public void testMethod() throws Exception {
        Method method = AnIntClass.class.getDeclaredMethod("setValue", new Class[]{Integer.TYPE});
        String expected =
                "<method>\n" +
                "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n" +
                "  <name>setValue</name>\n" +
                "  <parameter-types>\n" +
                "    <class>int</class>\n" +
                "  </parameter-types>\n" +
                "</method>";
        assertBothWays(method, expected);
    }

    public void testSupportsPrivateMethods() throws NoSuchMethodException {
        Method method = AnIntClass.class.getDeclaredMethod("privateMethod", new Class[]{});
        String expected =
                "<method>\n" +
                "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n" +
                "  <name>privateMethod</name>\n" +
                "  <parameter-types/>\n" +
                "</method>";
        assertBothWays(method, expected);
    }

    public void testSupportsConstructor() throws NoSuchMethodException {
        Constructor constructor = AnIntClass.class.getDeclaredConstructor(new Class[] { int.class });
        String expected =
                "<constructor>\n" +
                "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n" +
                "  <parameter-types>\n" +
                "    <class>int</class>\n" +
                "  </parameter-types>\n" +
                "</constructor>";
        assertBothWays(constructor, expected);
    }

    static class AnIntClass {
        private int value = 0;

        protected AnIntClass(int integer) {
            this.value = integer;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int integer) {
            this.value = integer;
        }

        private void privateMethod() {
        }
    }
}
