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
 * Class containing 5 basic types.
 * 
 * @since 1.4
 */
public class Five extends One {
    
    private int two;
    private boolean three;
    private char four;
    private StringBuffer five;

    public Five(String one, int two, boolean three, char four, StringBuffer five) {
        super(one);
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    public boolean equals(Object obj) {
        Five five = (Five)obj;
        return super.equals(obj) && two == five.two && three == five.three && four == five.four && this.five.toString().equals(five.five.toString());
    }

    public int hashCode() {
        return super.hashCode() + two + new Boolean(three).hashCode() + five.toString().hashCode();
    }
}