package com.thoughtworks.xstream.converters.basic;

public class CharConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return new Character(str.charAt(0));
    }

}
