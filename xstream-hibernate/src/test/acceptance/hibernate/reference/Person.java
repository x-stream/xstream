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

/**
 * @author Jaime Metcher
 */
public class Person extends BaseDomainObject {

    private Department department;
    private Site site;

    protected Person() {
    }

    public Person(final String name, final Department department, final Site site) {
        this.name = name;
        this.department = department;
        this.site = site;

        department.getPeople().add(this);
        site.getPeople().add(this);
    }

    public Site getSite() {
        return site;
    }

    public Department getDepartment() {
        return department;
    }

}
