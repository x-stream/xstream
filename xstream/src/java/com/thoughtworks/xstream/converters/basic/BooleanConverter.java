package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a boolean primitive or java.lang.Boolean wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class BooleanConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    protected Object fromString(String str) {
        return str.equals("true") ? Boolean.TRUE : Boolean.FALSE;
    }

}
