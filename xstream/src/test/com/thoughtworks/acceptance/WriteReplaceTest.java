package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StatusEnum;
import com.thoughtworks.acceptance.objects.ColorEnum;

import java.io.*;

public class WriteReplaceTest extends AbstractAcceptanceTest {

    public static class Thing extends StandardObject implements Serializable {

        int a;
        int b;

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
}
