/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.acceptance.objects.StandardObject;

public class X extends StandardObject {
    public String aStr;
    public int anInt;
    public Y innerObj;

    public X() {
    }

    public X(int anInt) {
        this.anInt = anInt;
    }
}
