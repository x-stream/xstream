package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

/**
 * <p>
 * A class {@link Serializable} {@link Parent} class implements
 * <code>writeObject()</code> and holds a {@link Child} class that also
 * implements <code>writeObject()</code>
 * </p>
 * 
 * @author <a href="mailto:cleclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class SerializationNestedWriteObjectsTest extends AbstractAcceptanceTest {

    public static class Child implements Serializable {

        private int i = 3;

        public Child(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException,
                ClassNotFoundException {
            in.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }
    }

    public static class Parent implements Serializable {

        private String name;

        private transient Child child;

        public Parent(String name, Child child) {
            this.name = name;
            this.child = child;
        }

        public Child getChild() {
            return child;
        }

        public String getName() {
            return name;
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException,
                ClassNotFoundException {
            this.child = (Child) in.readObject();
            in.defaultReadObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(this.child);
            out.defaultWriteObject();
        }
    }

    public void testObjectInputStream() throws Exception {
        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        String sourceXml = ""
                + "<object-stream>\n"
                + "  <parent serialization=\"custom\">\n"
                + "    <parent>\n"
                + "      <child serialization=\"custom\">\n"
                + "        <child>\n"
                + "          <default>\n"
                + "            <i>1</i>\n"
                + "          </default>\n"
                + "        </child>\n"
                + "      </child>\n"
                + "      <default>\n"
                + "        <name>ze-name</name>\n"
                + "      </default>\n"
                + "    </parent>\n"
                + "  </parent>\n"
                + "</object-stream>";

        ObjectInputStream objectInputStream = xstream.createObjectInputStream(new StringReader(
                sourceXml));

        Parent parent = (Parent) objectInputStream.readObject();

        assertEquals("ze-name", parent.getName());
        assertEquals(1, parent.getChild().getI());
    }

    public void testObjectOutputStream() throws Exception {
        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        String expectedXml = ""
                + "<object-stream>\n"
                + "  <parent serialization=\"custom\">\n"
                + "    <parent>\n"
                + "      <child serialization=\"custom\">\n"
                + "        <child>\n"
                + "          <default>\n"
                + "            <i>1</i>\n"
                + "          </default>\n"
                + "        </child>\n"
                + "      </child>\n"
                + "      <default>\n"
                + "        <name>ze-name</name>\n"
                + "      </default>\n"
                + "    </parent>\n"
                + "  </parent>\n"
                + "</object-stream>";

        Parent parent = new Parent("ze-name", new Child(1));
        StringWriter stringWriter = new StringWriter();
        ObjectOutputStream os = xstream.createObjectOutputStream(stringWriter);
        os.writeObject(parent);
        os.close();
        String actualXml = stringWriter.getBuffer().toString();
        assertEquals(expectedXml, actualXml);
    }

    public void testToXML() {

        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        String expected = ""
                + "<parent serialization=\"custom\">\n"
                + "  <parent>\n"
                + "    <child serialization=\"custom\">\n"
                + "      <child>\n"
                + "        <default>\n"
                + "          <i>1</i>\n"
                + "        </default>\n"
                + "      </child>\n"
                + "    </child>\n"
                + "    <default>\n"
                + "      <name>ze-name</name>\n"
                + "    </default>\n"
                + "  </parent>\n"
                + "</parent>";

        Parent parent = new Parent("ze-name", new Child(1));

        assertBothWays(parent, expected);
    }
}