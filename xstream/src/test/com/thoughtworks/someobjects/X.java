package com.thoughtworks.someobjects;

public class X {
    public String aStr;
    public int anInt;
    public Y innerObj;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof X)) return false;

        final X x = (X) o;

        if (anInt != x.anInt) return false;
        if (aStr != null ? !aStr.equals(x.aStr) : x.aStr != null) return false;
        if (innerObj != null ? !innerObj.equals(x.innerObj) : x.innerObj != null) return false;

        return true;
    }

    public String toString() {
        return "X:{" + aStr + "," + anInt + "(" + innerObj + ")}";
    }
}
