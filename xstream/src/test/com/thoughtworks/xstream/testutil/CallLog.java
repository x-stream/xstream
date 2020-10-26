/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.testutil;

import junit.framework.Assert;

public class CallLog {

    private StringBuffer expected = new StringBuffer();
    private StringBuffer actual = new StringBuffer();

    public void expect(String message) {
        expected.append(message).append('\n');
    }

    public void actual(String message) {
        actual.append(message).append('\n');
    }

    public void verify() {
        Assert.assertEquals(expected.toString(), actual.toString());
        reset();
    }

    public void reset() {
        expected = new StringBuffer();
        actual = new StringBuffer();
    }

}
