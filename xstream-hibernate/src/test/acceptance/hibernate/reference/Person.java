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
