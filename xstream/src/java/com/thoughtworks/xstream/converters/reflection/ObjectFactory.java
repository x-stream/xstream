package com.thoughtworks.xstream.converters.reflection;

/**
 * An ObjectFactory is responsible for instantiating a new instance of a type.
 */
public interface ObjectFactory {
    Object create(Class type);
}
