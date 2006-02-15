package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a long primitive or java.lang.Long wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class LongConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    public Object fromString(String str) {
        return Long.valueOf(str);
    }

}
