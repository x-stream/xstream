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

package acceptance.hibernate.reference;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Jaime Metcher
 */
public class Department extends BaseDomainObject {

    @SuppressWarnings("unused")
    private Division division;

    private final Set<Person> people = new HashSet<Person>(0);

    protected Department() {
    }

    public Department(final String name, final Division division) {
        this.name = name;
        this.division = division;
        division.getDepartments().add(this);
    }

    public Set<Person> getPeople() {
        return people;
    }
}
