/*
 * Copyright (C) 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. April 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance.objects;

import java.util.List;


public class Category<T> {

    String name;
    String id;
    List<T> products;

    public Category(final String name, final String id) {
        super();
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<T> getProducts() {
        return products;
    }

    public void setProducts(final List<T> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        String ret = "[" + name + ", " + id;
        if (products != null) {
            ret += "\n{";
	    ret = products.stream().map((product) -> product + "\n").reduce(ret, String::concat);
            ret += "}";
        }
        ret += "]";
        return ret;
    }

}
