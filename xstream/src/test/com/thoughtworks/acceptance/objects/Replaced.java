/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. October 2008 by Joerg Schaible
 */

/**
 * @author Joe Walnes
 */
package com.thoughtworks.acceptance.objects;


public class Replaced extends StandardObject {
    String replacedValue;

    public Replaced() {
    }

    public Replaced(String replacedValue) {
        this.replacedValue = replacedValue;
    }

    private Object readResolve() {
        return new Original(replacedValue.toLowerCase());
    }
}