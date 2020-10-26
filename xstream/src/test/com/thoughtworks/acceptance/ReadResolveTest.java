/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.thoughtworks.acceptance.objects.StatusEnum;


/**
 * @author Chris Kelly
 * @author Joe Walnes
 */
public class ReadResolveTest extends AbstractAcceptanceTest {

    public void testReadResolveWithDefaultSerialization() throws IOException, ClassNotFoundException {
        final StatusEnum status = StatusEnum.STARTED;

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(status);

        final byte[] bArray = bout.toByteArray();
        StatusEnum rStatus = null;
        ObjectInputStream in = null;

        final ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        rStatus = (StatusEnum)in.readObject();
        assertNotNull(rStatus);

        assertSame(status, rStatus);
    }

    public void testReadResolveWithXStream() {
        final StatusEnum status = StatusEnum.STARTED;

        final String xml = xstream.toXML(status);
        final StatusEnum rStatus = (StatusEnum)xstream.fromXML(xml);

        assertSame(status, rStatus);
    }

    public static class ResolveToNull implements Serializable {
        private static final long serialVersionUID = 201412L;
        final String name;

        public ResolveToNull(final String name) {
            this.name = name;
        }

        private Object readResolve() {
            return null;
        }
    }

    public void testResolveToNull() throws IOException, ClassNotFoundException {
        final ResolveToNull obj = new ResolveToNull("test");

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(obj);

        final byte[] bArray = bout.toByteArray();
        ObjectInputStream in = null;
        final ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        assertNull(in.readObject());

        xstream.alias("toNull", ResolveToNull.class);
        assertNull(xstream.fromXML("<toNull><name>test</name></toNull>"));
    }
}
