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
