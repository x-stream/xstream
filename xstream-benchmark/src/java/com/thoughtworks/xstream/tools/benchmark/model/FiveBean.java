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
 * JavaBean class containing 5 basic types.
 * 
 * @since 1.4
 */
public class FiveBean extends OneBean {
    
    private int two;
    private boolean three;
    private char four;
    private StringBuffer five;

    public int getTwo() {
        return this.two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public boolean isThree() {
        return this.three;
    }

    public void setThree(boolean three) {
        this.three = three;
    }

    public char getFour() {
        return this.four;
    }

    public void setFour(char four) {
        this.four = four;
    }

    public StringBuffer getFive() {
        return this.five;
    }

    public void setFive(StringBuffer five) {
        this.five = five;
    }

    public boolean equals(Object obj) {
        FiveBean five = (FiveBean)obj;
        return super.equals(obj) && two == five.two && three == five.three && four == five.four && this.five.toString().equals(five.five.toString());
    }

    public int hashCode() {
        return super.hashCode() + two + new Boolean(three).hashCode() + five.toString().hashCode();
    }
}