/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. May 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StatusEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Chris Kelly
 * @author Joe Walnes
 */ 
public class ReadResolveTest extends AbstractAcceptanceTest {

    public void testReadResolveWithDefaultSerialization() throws IOException, ClassNotFoundException {
        StatusEnum status = StatusEnum.STARTED;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(status);

        byte[] bArray = bout.toByteArray();
        StatusEnum rStatus = null;
        ObjectInputStream in = null;

        ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        rStatus = (StatusEnum) in.readObject();
        assertNotNull(rStatus);

        assertSame(status, rStatus);
    }

    public void testReadResolveWithXStream() {
        StatusEnum status = StatusEnum.STARTED;

        String xml = xstream.toXML(status);
        StatusEnum rStatus = (StatusEnum) xstream.fromXML(xml);

        assertSame(status, rStatus);
    }
    
    public static class ResolveToNull implements Serializable {
        private String name;
        public ResolveToNull(String name) {
            this.name = name;
        }
        private Object readResolve() {
            return null;
        }
    }
    
    public void testResolveToNull() throws IOException, ClassNotFoundException {
        ResolveToNull obj = new ResolveToNull("test");
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(obj);
        
        byte[] bArray = bout.toByteArray();
        ObjectInputStream in = null;
        ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        assertNull(in.readObject());
        
        xstream.alias("toNull", ResolveToNull.class);
        assertNull(xstream.fromXML("<toNull><name>test</name></toNull>"));
    }
}
