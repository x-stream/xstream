package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.acceptance.StandardObject;

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
