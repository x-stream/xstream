/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2011 by Jaime Metcher
 */
package acceptance.hibernate.reference;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Jaime Metcher
 */
public class Department extends BaseDomainObject {

    private Division division;

    private Set people = new HashSet(0);

    protected Department() {
    }

    public Department(final String name, final Division division) {
        this.name = name;
        this.division = division;
        division.getDepartments().add(this);
    }

    public Set getPeople() {
        return people;
    }
}
