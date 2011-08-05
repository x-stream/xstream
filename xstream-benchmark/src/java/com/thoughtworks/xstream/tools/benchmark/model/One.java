/*
 * Copyright (C) 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.model;


/**
 * Class containing one basic type.
 * 
 * @since 1.4
 */
public class One {
    
    private String one;
    
    public One(String one) {
        this.one = one;
    }

    public boolean equals(Object obj) {
        return one.equals(((One)obj).one);
    }

    public int hashCode() {
        return one.hashCode() >>> 1;
    }
}