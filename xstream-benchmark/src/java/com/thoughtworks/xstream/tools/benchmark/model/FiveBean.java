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

package com.thoughtworks.xstream.tools.benchmark.model;


/**
 * JavaBean class containing 5 basic types.
 * 
 * @since 1.4
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
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
        return super.hashCode() + two + Boolean.valueOf(three).hashCode() + five.toString().hashCode();
    }
}
