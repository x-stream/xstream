package com.thoughtworks.xstream.objecttree.reflection;

import com.thoughtworks.xstream.objecttree.ObjectAccessException;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SunReflectionObjectFactory implements ObjectFactory {

    private ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    public Object create(Class type) {
        try {
            Constructor javaLangObjectConstructor = Object.class.getDeclaredConstructor(new Class[0]);
            Constructor customConstructor = reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);
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

}
