/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * An ArrayList of user defined class ({@link Person}) instances to serialize.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class ListTarget implements Target {

    private List list = new ArrayList();

    public ListTarget(int size) {
       for (int i = 0; i < size; i++) {
           Person person = new Person();
           person.firstName = "First name " + i;
           person.lastName = "Last name " + i;
           list.add(person);
       }
    }

    public String toString() {
        return "List of " + list.size() + " user defined objects";
    }

    public Object target() {
        return list;
    }

    public boolean isEqual(Object other) {
        return list.equals(other);
    }
}
