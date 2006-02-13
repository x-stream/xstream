package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class WriteReplaceTest extends AbstractAcceptanceTest {

    public static class Thing extends StandardObject implements Serializable {

        int a;
        int b;

        public Thing() {
        }

        public Thing(int a, int b) {
            this.a = a;
            this.b = b;
        }

        private Object writeReplace() {
            return new Thing(a * 1000, b * 1000);
        }

        private Object readResolve() {
            return new Thing(a / 1000, b / 1000);
        }

    }

    public void testReplacesAndResolves() {
        xstream.alias("thing", Thing.class);

        Thing thing = new Thing(3, 6);

        String expectedXml = ""
                + "<thing>\n"
                + "  <a>3000</a>\n"
                + "  <b>6000</b>\n"
                + "</thing>";

        assertBothWays(thing, expectedXml);
    }

    public static class Original extends StandardObject {
        String originalValue;

        public Original() {
        }

        public Original(String originalValue) {
            this.originalValue = originalValue;
        }

        private Object writeReplace() {
            return new Replaced(originalValue.toUpperCase());
        }
    }

    public static class Replaced extends StandardObject {
        String replacedValue;

        public Replaced() {
        }

        public Replaced(String replacedValue) {
            this.replacedValue = replacedValue;
        }

        private Object readResolve() {
            return new Original(replacedValue.toLowerCase());
        }
    }

    public void testAllowsDifferentTypeToBeSubstituted() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        Original in = new Original("hello world");

        String expectedXml = ""
                + "<original-class resolves-to=\"replaced-class\">\n"
                + "  <replacedValue>HELLO WORLD</replacedValue>\n"
                + "</original-class>";

        assertBothWays(in, expectedXml);
    }

    public static class OriginalSerializable extends StandardObject {
        String originalValue;

        public OriginalSerializable() {
        }

        public OriginalSerializable(String originalValue) {
            this.originalValue = originalValue;
        }

        private Object writeReplace() {
            return new ReplacedSerializable(originalValue.toUpperCase());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public static class ReplacedSerializable extends StandardObject {
        String replacedValue;

        public ReplacedSerializable() {
        }

        public ReplacedSerializable(String replacedValue) {
            this.replacedValue = replacedValue;
        }

        private Object readResolve() {
            return new OriginalSerializable(replacedValue.toLowerCase());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testAllowsDifferentTypeToBeSubstitutedForCustomSerializableObjects() {
        xstream.alias("original-serializable-class", OriginalSerializable.class);
        xstream.alias("replaced-serializable-class", ReplacedSerializable.class);

        OriginalSerializable in = new OriginalSerializable("hello world");

        String expectedXml = ""
                + "<original-serializable-class resolves-to=\"replaced-serializable-class\" serialization=\"custom\">\n"
                + "  <replaced-serializable-class>\n"
                + "    <default>\n"
                + "      <replacedValue>HELLO WORLD</replacedValue>\n"
                + "    </default>\n"
                + "  </replaced-serializable-class>\n"
                + "</original-serializable-class>";

        assertBothWays(in, expectedXml);
    }

}
