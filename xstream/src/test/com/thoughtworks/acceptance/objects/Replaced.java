/*
 * Copyright (C) 2008, 2018 XStream Committers.
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
    private static final long serialVersionUID = 200810L;
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
