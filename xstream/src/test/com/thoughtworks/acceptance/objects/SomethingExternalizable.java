/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. July 2011 by Joerg Schaible, factored out of ExternalizableTest.
 */
package com.thoughtworks.acceptance.objects;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class SomethingExternalizable extends StandardObject implements Externalizable {

    private String first;
    private String last;
    private String nothing = null;
    private String constant = "XStream";

    public SomethingExternalizable() {
    }

    public SomethingExternalizable(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(first.length());
        out.writeObject(first + last);
        out.writeObject(nothing);
        out.writeObject(constant);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int offset = in.readInt();
        String full = (String) in.readObject();
        first = full.substring(0, offset);
        last = full.substring(offset);
        nothing = (String) in.readObject();
        constant = (String) in.readObject();
    }
}