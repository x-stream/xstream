/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. May 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.model;


/**
 * JavaBean class containing one basic type.
 * 
 * @since 1.4
 */
public class OneBean {
    
    private String one;

    public String getOne() {
        return this.one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public boolean equals(Object obj) {
        return one.equals(((OneBean)obj).one);
    }

    public int hashCode() {
        return one.hashCode() >>> 1;
    }
}