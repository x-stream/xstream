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

package com.thoughtworks.xstream.tools.benchmark.targets;

import java.util.Date;
import java.io.Serializable;

/**
 * @see UserDefinedClassTarget
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
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
