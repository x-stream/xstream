package com.thoughtworks.someobjects;

import java.util.ArrayList;
import java.util.List;

public class WithList {

    public List things = new ArrayList();

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WithList)) return false;

        final WithList withList = (WithList) o;

        if (things != null ? !things.equals(withList.things) : withList.things != null) return false;
        if (!things.getClass().equals(withList.things.getClass())) return false;

        return true;
    }

    public int hashCode() {
        return (things != null ? things.hashCode() : 0);
    }

    public String toString() {
        return "WithList:" + things;
    }
}
