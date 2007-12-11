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

import java.util.Date;

/**
 * A user defined class ({@link Person}) to serialize that contains a few simple fields.  
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class UserDefinedClassTarget implements Target {

    private final Person person;

    public UserDefinedClassTarget() {
        person = new Person();
        person.firstName = "Joe";
        person.lastName = "Walnes";
        person.dateOfBirth = new Date();
    }

    public String toString() {
        return "User defined class";
    }

    public Object target() {
        return person;
    }

    public boolean isEqual(Object other) {
        return person.equals(other);
    }
}
