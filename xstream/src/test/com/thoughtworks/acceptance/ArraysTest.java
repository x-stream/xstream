package com.thoughtworks.acceptance;

public class ArraysTest extends AbstractAcceptanceTest {

    public void testStringArray() {
        String[] array = new String[]{"a", "b", "c"};

        String expected = "" +
                "<string-array>\n" +
                "  <string>a</string>\n" +
                "  <string>b</string>\n" +
                "  <string>c</string>\n" +
                "</string-array>";

        assertBothWays(array, expected);
    }

    public void testPrimitiveArray() {
        int[] array = new int[]{1, 2};

        String expected = "" +
                "<int-array>\n" +
                "  <int>1</int>\n" +
                "  <int>2</int>\n" +
                "</int-array>";

        assertBothWays(array, expected);
    }

    public void testBoxedTypeArray() {
        Integer[] array = new Integer[]{new Integer(1), new Integer(2)};

        String expected = "" +
                "<java.lang.Integer-array>\n" +
                "  <int>1</int>\n" +
                "  <int>2</int>\n" +
                "</java.lang.Integer-array>";

        assertBothWays(array, expected);
    }

    public static class X extends StandardObject {
        String s = "hi";
    }

    public void testCustomObjectArray() {

        X[] array = new X[]{new X(), new X()};

        String expected = "" +
                "<com.thoughtworks.acceptance.ArraysTest_-X-array>\n" +
                "  <com.thoughtworks.acceptance.ArraysTest_-X>\n" +
                "    <s>hi</s>\n" +
                "  </com.thoughtworks.acceptance.ArraysTest_-X>\n" +
                "  <com.thoughtworks.acceptance.ArraysTest_-X>\n" +
                "    <s>hi</s>\n" +
                "  </com.thoughtworks.acceptance.ArraysTest_-X>\n" +
                "</com.thoughtworks.acceptance.ArraysTest_-X-array>";

        assertBothWays(array, expected);
    }

    public void testArrayOfMixedTypes() {

        Object[] array = new Number[]{new Long(2), new Integer(3)};

        String expected = "" +
                "<number-array>\n" +
                "  <long>2</long>\n" +
                "  <int>3</int>\n" +
                "</number-array>";

        assertBothWays(array, expected);

    }

    public void testEmptyArray() {
        int[] array = new int[]{};

        String expected = "<int-array/>";

        assertBothWays(array, expected);

    }

    public void testUninitializedArray() {
        String[] array = new String[4];
        array[0] = "zero";
        array[2] = "two";

        String expected = "" +
                "<string-array>\n" +
                "  <string>zero</string>\n" +
                "  <null/>\n" +
                "  <string>two</string>\n" +
                "  <null/>\n" +
                "</string-array>";

        assertBothWays(array, expected);

    }

    public void testArrayInCustomObject() {
        ObjWithArray objWithArray = new ObjWithArray();
        objWithArray.strings = new String[]{"hi", "bye"};
        xstream.alias("owa", ObjWithArray.class);
        String expected = "" +
                "<owa>\n" +
                "  <strings>\n" +
                "    <string>hi</string>\n" +
                "    <string>bye</string>\n" +
                "  </strings>\n" +
                "</owa>";
        assertBothWays(objWithArray, expected);
    }

    public static class ObjWithArray extends StandardObject {
        String[] strings;
    }

    public void testDeserializingObjectWhichContainsAPrimitiveLongArray() {
        String xml =
                "<owla>" +
                "  <bits class=\"long-array\">" +
                "    <long>0</long>" +
                "    <long>1</long>" +
                "    <long>2</long>" +
                "  </bits>" +
                "</owla>";

        xstream.alias("owla", ObjectWithLongArray.class);

        ObjectWithLongArray o = (ObjectWithLongArray) xstream.fromXML(xml);

        assertEquals(o.bits[0], 0);
        assertEquals(o.bits[1], 1);
        assertEquals(o.bits[2], 2);
    }

    public static class ObjectWithLongArray {
        long[] bits;
    }

    public void testMultidimensionalArray() {
        int[][] array = new int[3][2];
        array[0][0] = 2;
        array[0][1] = 4;
        array[1][0] = 8;
        array[1][1] = 16;
        array[2] = new int[3];
        array[2][0] = 33;
        array[2][1] = 66;
        array[2][2] = 99;

        String expectedXml = "" +
                "<int-array-array>\n" +
                "  <int-array>\n" +
                "    <int>2</int>\n" +
                "    <int>4</int>\n" +
                "  </int-array>\n" +
                "  <int-array>\n" +
                "    <int>8</int>\n" +
                "    <int>16</int>\n" +
                "  </int-array>\n" +
                "  <int-array>\n" +
                "    <int>33</int>\n" +
                "    <int>66</int>\n" +
                "    <int>99</int>\n" +
                "  </int-array>\n" +
                "</int-array-array>";

        String actualXml = xstream.toXML(array);
        assertEquals(expectedXml, actualXml);

        int[][] result = (int[][]) xstream.fromXML(actualXml);
        assertEquals(2, result[0][0]);
        assertEquals(4, result[0][1]);
        assertEquals(8, result[1][0]);
        assertEquals(16, result[1][1]);
        assertEquals(99, result[2][2]);
        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(2, result[1].length);
        assertEquals(3, result[2].length);
    }

    public static class Thing {
    }

    public static class SpecialThing extends Thing {
    }

    public void testMultidimensionalArrayOfMixedTypes() {
        xstream.alias("thing", Thing.class);
        xstream.alias("special-thing", SpecialThing.class);

        Object[][] array = new Object[2][2];
        array[0][0] = new Object();
        array[0][1] = "a string";
        array[1] = new Thing[2];
        array[1][0] = new Thing();
        array[1][1] = new SpecialThing();
        String expectedXml = "" +
                "<object-array-array>\n" +
                "  <object-array>\n" +
                "    <object/>\n" +
                "    <string>a string</string>\n" +
                "  </object-array>\n" +
                "  <thing-array>\n" +
                "    <thing/>\n" +
                "    <special-thing/>\n" +
                "  </thing-array>\n" +
                "</object-array-array>";

        String actualXml = xstream.toXML(array);
        assertEquals(expectedXml, actualXml);

        Object[][] result = (Object[][]) xstream.fromXML(actualXml);
        assertEquals(Object.class, result[0][0].getClass());
        assertEquals("a string", result[0][1]);
        assertEquals(Thing.class, result[1][0].getClass());
        assertEquals(SpecialThing.class, result[1][1].getClass());
    }

    public static class NoOneLikesMe extends StandardObject {
        private int name;

        public NoOneLikesMe(int name) {
            this.name = name;
        }
    }

    public void testHandlesArrayClassesThatHaveNotYetBeenLoaded() {
        // Catch weirdness in classloader. 
        // Resolved by using Class.forName(x, false, classLoader), instead of classLoader.loadClass(x);
        String xml = ""
                + "<com.thoughtworks.acceptance.ArraysTest-NoOneLikesMe-array>\n"
                + "  <com.thoughtworks.acceptance.ArraysTest-NoOneLikesMe>\n"
                + "    <name>99</name>\n"
                + "  </com.thoughtworks.acceptance.ArraysTest-NoOneLikesMe>\n"
                + "</com.thoughtworks.acceptance.ArraysTest-NoOneLikesMe-array>";
        NoOneLikesMe[] result = (NoOneLikesMe[]) xstream.fromXML(xml);
        assertEquals(1, result.length);
        assertEquals(99, result[0].name);
    }

}
