package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

/**
 * Converts a java.lang.Class to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author Joe Walnes
 * @author Matthew Sandoz
 */
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
            return
                    str.equals("void") ? void.class :
                    str.equals("byte") ? byte.class :
                    str.equals("int") ? int.class :
                    str.equals("long") ? long.class :
                    str.equals("float") ? float.class :
                    str.equals("boolean") ? boolean.class :
                    str.equals("double") ? double.class :
                    str.equals("char") ? char.class :
                    classLoader.loadClass(str);
        } catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot load java class " + str, e);
        }
    }

}
