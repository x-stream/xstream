package com.thoughtworks.xstream.converters.reflection;


/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects
 * that can be constructed are limited.
 *
 * Can create: classes with public visibility, outer classes, static inner classes, classes with default constructors.
 * Cannot create: classes without public visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates the object.
 */
public class JavaReflectionObjectFactory implements ObjectFactory {
    public Object create(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }
}
