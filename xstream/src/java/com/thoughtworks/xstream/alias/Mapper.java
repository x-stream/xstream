package com.thoughtworks.xstream.alias;

public interface Mapper {

    /**
     * Whether this type is a simple immutable value (int, boolean, String, URL, etc.
     */
    boolean isImmutableValueType(Class type);

    Class defaultImplementationOf(Class type);

    String attributeForImplementationClass();
    String attributeForClassDefiningField();

}
