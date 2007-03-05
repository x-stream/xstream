package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a byte primitive or java.lang.Byte wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class ByteConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(byte.class) || type.equals(Byte.class);
    }

    public Object fromString(String str) {
    	int value = Integer.decode(str).intValue();
    	if(value < Byte.MIN_VALUE || value > 0xFF) {
    		throw new NumberFormatException("For input string: \"" + str + '"');
    	}
        return new Byte((byte)value);
    }

}
