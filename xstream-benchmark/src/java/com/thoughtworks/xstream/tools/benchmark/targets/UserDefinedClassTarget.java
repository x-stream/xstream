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

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.util.Date;

/**
 * A user defined class ({@link Person}) to serialize that contains a few simple fields.  
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
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
