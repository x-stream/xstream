package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.lang.Class to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author Joe Walnes
 * @author Matthew Sandoz
 */
public class JavaClassConverter extends AbstractSingleValueConverter {

    private ClassLoader classLoader;

    /**
     * @deprecated As of 1.1.1 - use other constructor and explicitly supply a ClassLoader.
     */
    public JavaClassConverter() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public JavaClassConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean canConvert(Class clazz) {
        return Class.class.equals(clazz); // :)
    }

    public String toString(Object obj) {
        return ((Class) obj).getName();
    }

    public Object fromString(String str) {
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
                    str.equals("short") ? short.class :
                    Class.forName(str, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot load java class " + str, e);
        }
    }

}
