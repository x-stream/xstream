package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a byte primitive or java.lang.Byre wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class ByteConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(byte.class) || type.equals(Byte.class);
    }

    protected Object fromString(String str) {
        return new Byte((byte) Integer.parseInt(str));
    }

}
