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

    class X extends StandardObject {
        String s = "hi";
    }

    public void testCustomObjectArray() {

        X[] array = new X[]{new X(), new X()};

        String expected = "" +
                "<com.thoughtworks.acceptance.ArraysTest-X-array>\n" +
                "  <com.thoughtworks.acceptance.ArraysTest-X>\n" +
                "    <s>hi</s>\n" +
                "  </com.thoughtworks.acceptance.ArraysTest-X>\n" +
                "  <com.thoughtworks.acceptance.ArraysTest-X>\n" +
                "    <s>hi</s>\n" +
                "  </com.thoughtworks.acceptance.ArraysTest-X>\n" +
                "</com.thoughtworks.acceptance.ArraysTest-X-array>";

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

    class ObjWithArray extends StandardObject {
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

        xstream.alias( "owla", ObjectWithLongArray.class );

        ObjectWithLongArray o = (ObjectWithLongArray) xstream.fromXML( xml );

        assertEquals( o.bits[0], 0 );
        assertEquals( o.bits[1], 1 );
        assertEquals( o.bits[2], 2 );
    }

    class ObjectWithLongArray {
        long[] bits;
    }
}
