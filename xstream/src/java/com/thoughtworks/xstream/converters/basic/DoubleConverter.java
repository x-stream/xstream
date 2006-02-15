package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a double primitive or java.lang.Double wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class DoubleConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(double.class) || type.equals(Double.class);
    }

    public Object fromString(String str) {
        return Double.valueOf(str);
    }

}
