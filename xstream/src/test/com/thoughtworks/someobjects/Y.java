package com.thoughtworks.someobjects;

public class Y {
    public String yField;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Y)) return false;

        final Y y = (Y) o;

        if (yField != null ? !yField.equals(y.yField) : y.yField != null) return false;

        return true;
    }

    public String toString() {
        return "Y:" + yField;
    }
}
