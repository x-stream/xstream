package com.thoughtworks.xstream.mapper;

public interface Mapper {

    /**
     * Whether this type is a simple immutable value (int, boolean, String, URL, etc.
     * Immutable types will be repeatedly written in the serialized stream, instead of using object references.
     */
    boolean isImmutableValueType(Class type);
    
    Class defaultImplementationOf(Class type);

    String attributeForImplementationClass();
    String attributeForClassDefiningField();
    String attributeForReadResolveField();


}
