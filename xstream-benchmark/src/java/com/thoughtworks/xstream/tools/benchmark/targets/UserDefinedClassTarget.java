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
