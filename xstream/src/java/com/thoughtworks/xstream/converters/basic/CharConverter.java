package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a char primitive or java.lang.Character wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class CharConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(char.class) || type.equals(Character.class);
    }

    protected Object fromString(String str) {
        return new Character(str.charAt(0));
    }

}
