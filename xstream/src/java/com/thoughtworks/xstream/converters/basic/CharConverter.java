package com.thoughtworks.xstream.converters.basic;

public class CharConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(char.class) || type.equals(Character.class);
    }

    protected Object fromString(String str) {
        return new Character(str.charAt(0));
    }

}
