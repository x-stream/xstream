/*
 * Copyright (C) 2011, 2018 XStream Committers.
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
public class Site extends BaseDomainObject {

    private final Set<Person> people = new HashSet<Person>(0);

    protected Site() {
    }

    public Site(final String name) {
        this.name = name;
    }

    public Set<Person> getPeople() {
        return people;
    }

}
