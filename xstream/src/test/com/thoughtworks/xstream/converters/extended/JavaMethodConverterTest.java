package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Method;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class JavaMethodConverterTest extends AbstractAcceptanceTest {

    public void testMethod() throws Exception{
        Method method = AnIntClass.class.getDeclaredMethod("setValue",new Class[] {Integer.TYPE});
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

    class AnIntClass {
        private int value = 0;
        public AnIntClass(int integer){
            this.value = integer;
        }        
        public int getValue() {
            return value;
        }
        public void setValue(int integer) {
            this.value = integer;
        }
    }
}
