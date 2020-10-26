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
            for (final T product : products) {
                ret += product + "\n";
            }
            ret += "}";
        }
        ret += "]";
        return ret;
    }

}
