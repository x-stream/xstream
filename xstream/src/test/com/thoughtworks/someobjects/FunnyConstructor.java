package com.thoughtworks.someobjects;

public class FunnyConstructor {
    public int i;

    public FunnyConstructor(int i) {
        this.i = i;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunnyConstructor)) return false;

        final FunnyConstructor z = (FunnyConstructor) o;

        if (i != z.i) return false;

        return true;
    }

    public String toString() {
        return "FunnyConstructor:" + i;
    }
}
