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

package com.thoughtworks.xstream.tools.benchmark.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serializable class containing 5 basic types.
 * 
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class SerializableFive extends SerializableOne {
    
    private static final long serialVersionUID = 1L;
    private int two;
    private boolean three;
    private char four;
    private StringBuffer five;

    public SerializableFive(String one, int two, boolean three, char four, StringBuffer five) {
        super(one);
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public boolean equals(Object obj) {
        SerializableFive five = (SerializableFive)obj;
        return super.equals(obj) && two == five.two && three == five.three && four == five.four && this.five.toString().equals(five.five.toString());
    }

    public int hashCode() {
        return super.hashCode() + two + Boolean.valueOf(three).hashCode() + five.toString().hashCode();
    }
}
