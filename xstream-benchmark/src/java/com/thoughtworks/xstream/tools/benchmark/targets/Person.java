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

import java.util.Date;
import java.io.Serializable;

/**
 * @see UserDefinedClassTarget
 */
public class Person implements Serializable {

    public String firstName;
    public String lastName;
    public Date dateOfBirth;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Person person = (Person) o;

        if (dateOfBirth != null ? !dateOfBirth.equals(person.dateOfBirth) : person.dateOfBirth != null) return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;
        return !(lastName != null ? !lastName.equals(person.lastName) : person.lastName != null);
    }

    public int hashCode() {
        int result;
        result = (firstName != null ? firstName.hashCode() : 0);
        result = 29 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 29 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        return result;
    }
}
