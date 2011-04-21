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
public class BaseDomainObject {

    protected Integer id;
    protected String name;

    public String getName() {
        return name;
    }

    public BaseDomainObject() {
        super();
    }

}
