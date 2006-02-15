package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a short primitive or java.lang.Short wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class ShortConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(short.class) || type.equals(Short.class);
    }

    public Object fromString(String str) {
        return Short.valueOf(str);
    }

}
