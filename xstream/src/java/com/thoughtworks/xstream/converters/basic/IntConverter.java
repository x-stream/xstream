package com.thoughtworks.xstream.converters.basic;

/**
 * Converts an int primitive or java.lang.Integer wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class IntConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    protected Object fromString(String str) {
        return Integer.decode(str);
    }

}
