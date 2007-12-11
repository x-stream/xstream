/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance.someobjects;


public class WithNamedList extends WithList {
    private String name;

    public WithNamedList(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}