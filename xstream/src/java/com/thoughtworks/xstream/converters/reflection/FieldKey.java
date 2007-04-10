package com.thoughtworks.xstream.converters.reflection;

/**
 * A field key.
 */
public class FieldKey {
    final String fieldName;
    final Class declaringClass;
    private Integer depth;
    final int order;

    public FieldKey(String fieldName, Class declaringClass, int order) {
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.order = order;
        Class c = declaringClass;
        int i = 0;
        while (c.getSuperclass() != null) {
            i++;
            c = c.getSuperclass();
        }
        depth = new Integer(i);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldKey)) return false;

        final FieldKey fieldKey = (FieldKey)o;

        if (declaringClass != null
            ? !declaringClass.equals(fieldKey.declaringClass)
            : fieldKey.declaringClass != null) return false;
        if (fieldName != null
            ? !fieldName.equals(fieldKey.fieldName)
            : fieldKey.fieldName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (fieldName != null ? fieldName.hashCode() : 0);
        result = 29 * result + (declaringClass != null ? declaringClass.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "FieldKey{"
            + "order="
            + order
            + ", writer="
            + depth
            + ", declaringClass="
            + declaringClass
            + ", fieldName='"
            + fieldName
            + "'"
            + "}";
    }

}