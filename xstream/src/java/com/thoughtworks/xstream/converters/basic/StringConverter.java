package com.thoughtworks.xstream.converters.basic;

public class StringConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(String.class);
    }

    protected Object fromString(String str) {
        return str;
    }

}
