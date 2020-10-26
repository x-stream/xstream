/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. August 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.thoughtworks.acceptance.objects.OwnerOfExternalizable;
import com.thoughtworks.acceptance.objects.SomethingExternalizable;
import com.thoughtworks.acceptance.objects.StandardObject;


public class ExternalizableTest extends AbstractAcceptanceTest {

    public void testExternalizable() {
        xstream.alias("something", SomethingExternalizable.class);

        final SomethingExternalizable in = new SomethingExternalizable("Joe", "Walnes");

        final String expected = ""
            + "<something>\n"
            + "  <int>3</int>\n"
            + "  <string>JoeWalnes</string>\n"
            + "  <null/>\n"
            + "  <string>XStream</string>\n"
            + "</something>";

        assertBothWays(in, expected);
    }

    public void testExternalizableAsFieldOfAnotherObject() {
        xstream.alias("something", SomethingExternalizable.class);
        xstream.alias("owner", OwnerOfExternalizable.class);

        final OwnerOfExternalizable in = new OwnerOfExternalizable();
        in.target = new SomethingExternalizable("Joe", "Walnes");

        final String expected = ""
            + "<owner>\n"
            + "  <target>\n"
            + "    <int>3</int>\n"
            + "    <string>JoeWalnes</string>\n"
            + "    <null/>\n"
            + "    <string>XStream</string>\n"
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

        public CircularExternalizable(final String name) {
            this.name = name;
        }

        public void setParent(final CircularExternalizable parent) {
            this.parent = parent;
            if (parent != null) {
                parent.child = this;
            }
        }

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            name = (String)in.readObject();
            parent = (CircularExternalizable)in.readObject();
            child = (CircularExternalizable)in.readObject();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(name);
            out.writeObject(parent);
            out.writeObject(child);
        }

        // StandardObject uses EqualsBuilder.reflectionEquals of commons-lang,
        // that does not handle circular dependencies
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof CircularExternalizable && name.equals(obj.toString());
        }

        @Override
        public int hashCode() {
            return name.hashCode() + 1;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public void testCircularExternalizable() {
        xstream.alias("elem", CircularExternalizable.class);

        final CircularExternalizable parent = new CircularExternalizable("parent");
        final CircularExternalizable child = new CircularExternalizable("child");
        child.setParent(parent);

        final String expected = ""
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

    public static class OtherOwner extends StandardObject {
        private static final long serialVersionUID = 201008L;
        Object member1;
        Object member2;

        public OtherOwner(final int i) {
            member1 = new InnerExternalizable1(i);
            member2 = new InnerExternalizable2(i);
        }

        private static class InnerExternalizable1 extends StandardObject implements Externalizable {
            private int i;

            public InnerExternalizable1() {
            }

            InnerExternalizable1(final int i) {
                this.i = i;
            }

            @Override
            public void writeExternal(final ObjectOutput out) throws IOException {
                out.writeInt(i);
            }

            @Override
            public void readExternal(final ObjectInput in) throws IOException {
                i = in.readInt();
            }
        };

        private static class InnerExternalizable2 extends StandardObject implements Externalizable {
            private int i;

            @SuppressWarnings("unused")
            private InnerExternalizable2() {
            }

            InnerExternalizable2(final int i) {
                this.i = i;
            }

            @Override
            public void writeExternal(final ObjectOutput out) throws IOException {
                out.writeInt(i);
            }

            @Override
            public void readExternal(final ObjectInput in) throws IOException {
                i = in.readInt();
            }
        };
    }

    public void testWithPrivateDefaultConstructor() {
        final String name1 = OtherOwner.class.getDeclaredClasses()[0].getName();
        final String name2 = OtherOwner.class.getDeclaredClasses()[1].getName();
        xstream.alias("owner", OtherOwner.class);
        xstream.alias("inner" + name1.charAt(name1.length() - 1), OtherOwner.class.getDeclaredClasses()[0]);
        xstream.alias("inner" + name2.charAt(name2.length() - 1), OtherOwner.class.getDeclaredClasses()[1]);

        final OtherOwner owner = new OtherOwner(42);

        final String expected = ""
            + "<owner>\n"
            + "  <member1 class=\"inner1\">\n"
            + "    <int>42</int>\n"
            + "  </member1>\n"
            + "  <member2 class=\"inner2\">\n"
            + "    <int>42</int>\n"
            + "  </member2>\n"
            + "</owner>";

        assertBothWays(owner, expected);
    }
}
