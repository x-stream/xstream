package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

public class JavaClassConverter extends AbstractBasicConverter {

    private ClassLoader classLoader;

    public JavaClassConverter() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public JavaClassConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean canConvert(Class clazz) {
        return Class.class.equals(clazz); // :)
    }

    protected String toString(Object obj) {
        return ((Class) obj).getName();
    }

    protected Object fromString(String str) {
        try {
            return classLoader.loadClass(str);
        } catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot load java class " + str, e);
        }
    }

}
