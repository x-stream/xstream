package com.thoughtworks.xstream.converters.reflection;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Instantiates a new object on the Sun JVM by bypassing the constructor (meaning code in the constructor
 * will never be executed and parameters do not have to be known). This is the same method used by the internals of
 * standard Java serialization, but relies on internal Sun code that may not be present on all JVMs.
 */
public class Sun14ReflectionProvider extends PureJavaReflectionProvider {

    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    private static final Map constructorCache = new HashMap();

    public Object newInstance(Class type) {
        try {
            Constructor customConstructor = getMungedConstructor(type);
            Object newValue = customConstructor.newInstance(new Object[0]);
            return newValue;
        } catch (NoSuchMethodException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (SecurityException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    private Constructor getMungedConstructor(Class type) throws NoSuchMethodException {
        if (!constructorCache.containsKey(type)) {
            Constructor javaLangObjectConstructor = Object.class.getDeclaredConstructor(new Class[0]);
            Constructor customConstructor = reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);
            constructorCache.put(type, customConstructor);
        }
        return (Constructor) constructorCache.get(type);
    }

}
