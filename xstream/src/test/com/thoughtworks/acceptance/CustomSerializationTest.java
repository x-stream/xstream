package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CustomSerializationTest extends AbstractAcceptanceTest {

    public static class ObjectWithCustomSerialization extends StandardObject implements Serializable {

        private int a;
        private transient int b;
        private transient String c;
        private transient Object d;
        private transient Software e;

        public ObjectWithCustomSerialization(int a, int b, String c, Software e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.e = e;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            b = in.readInt();
            in.defaultReadObject();
            c = (String) in.readObject();
            d = in.readObject();
            e = (Software) in.readObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(b);
            out.defaultWriteObject();
            out.writeObject(c);
            out.writeObject(d);
            out.writeObject(e);
        }

    }

    public void testWritesCustomFieldsToStream() {
        ObjectWithCustomSerialization obj = new ObjectWithCustomSerialization(1, 2, "hello", new Software("tw", "xs"));
        xstream.alias("custom", ObjectWithCustomSerialization.class);
        xstream.alias("software", Software.class);

        String expectedXml = ""
                + "<custom>\n"
                + "  <stream.int>2</stream.int>\n"
                + "  <a>1</a>\n"
                + "  <stream.string>hello</stream.string>\n"
                + "  <stream.null/>\n"
                + "  <stream.software>\n"
                + "    <vendor>tw</vendor>\n"
                + "    <name>xs</name>\n"
                + "  </stream.software>\n"
                + "</custom>";

        assertEquals(expectedXml, xstream.toXML(obj));
    }

}
