package com.thoughtworks.acceptance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ExternalizableTest extends AbstractAcceptanceTest {

    public static class SomethingExternalizable extends StandardObject implements Externalizable {

        private String first;
        private String last;

        public SomethingExternalizable() {
        }

        public SomethingExternalizable(String first, String last) {
            this.first = first;
            this.last = last;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(first.length());
            out.writeObject(first + last);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            int offset = in.readInt();
            String full = (String) in.readObject();
            first = full.substring(0, offset);
            last = full.substring(offset);
        }
    }

    public void testExternalizable() {
        xstream.alias("something", SomethingExternalizable.class);
        
        SomethingExternalizable in = new SomethingExternalizable("Joe", "Walnes");

        String expected = ""
                + "<something>\n"
                + "  <int>3</int>\n"
                + "  <string>JoeWalnes</string>\n"
                + "</something>";

        assertBothWays(in, expected);
    }

    static class Owner extends StandardObject {
        SomethingExternalizable target;
    }

    public void testExternalizableAsFieldOfAnotherObject() {
        xstream.alias("something", SomethingExternalizable.class);
        xstream.alias("owner", Owner.class);

        Owner in = new Owner();
        in.target = new SomethingExternalizable("Joe", "Walnes");

        String expected = ""
                + "<owner>\n"
                + "  <target>\n"
                + "    <int>3</int>\n"
                + "    <string>JoeWalnes</string>\n"
                + "  </target>\n"
                + "</owner>";

        assertBothWays(in, expected);
    }
    
    public static class CircularExternalizable implements Externalizable {
        private String name;
        private CircularExternalizable parent;
        private CircularExternalizable child;
        
        public CircularExternalizable() {
        }
        
        public CircularExternalizable(String name) {
            this.name = name;
        }
        
        public void setParent(CircularExternalizable parent) {
            this.parent = parent;
            if (parent != null) {
                parent.child = this;
            }
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            name = (String)in.readObject();
            parent = (CircularExternalizable)in.readObject();
            child = (CircularExternalizable)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(name);
            out.writeObject(parent);
            out.writeObject(child);
        }

        // StandardObject uses EqualsBuilder.reflectionEquals of commons-lang, 
        // that does not handle circular dependencies
        public boolean equals(Object obj) {
            return obj instanceof CircularExternalizable && name.equals(obj.toString());
        }

        public int hashCode() {
            return name.hashCode()+1;
        }

        public String toString() {
            return name;
        }
        
    }

    public void testCircularExternalizable() {
        xstream.alias("elem", CircularExternalizable.class);
        
        CircularExternalizable parent = new CircularExternalizable("parent");
        CircularExternalizable child = new CircularExternalizable("child");
        child.setParent(parent);
        
        String expected = ""
            + "<elem>\n"
            + "  <string>parent</string>\n"
            + "  <null/>\n"
            + "  <elem>\n"
            + "    <string>child</string>\n"
            + "    <elem reference=\"../..\"/>\n"
            + "    <null/>\n"
            + "  </elem>\n"
            + "</elem>";

        assertBothWays(parent, expected);
    }
}
