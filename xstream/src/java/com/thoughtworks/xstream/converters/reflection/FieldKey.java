package com.thoughtworks.xstream.converters.reflection;

/**
 * A field key.
 */
public class FieldKey {
    final private String fieldName;
    final private Class declaringClass;
    final private int depth;
    final private int order;

    public FieldKey(String fieldName, Class declaringClass, int order) {
        if (fieldName == null || declaringClass == null) {
            throw new IllegalArgumentException("fieldName or declaringClass is null");
        }
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.order = order;
        Class c = declaringClass;
        int i = 0;
        while (c.getSuperclass() != null) {
            i++;
            c = c.getSuperclass();
        }
        depth = i;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Class getDeclaringClass() {
        return this.declaringClass;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldKey)) return false;

        final FieldKey fieldKey = (FieldKey)o;

        if (!declaringClass.equals(fieldKey.declaringClass)) 
            return false;
        if (!fieldName.equals(fieldKey.fieldName)) 
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = fieldName.hashCode();
        result = 29 * result +declaringClass.hashCode();
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