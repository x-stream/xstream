package com.thoughtworks.xstream.converters.basic;

public class ShortConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(short.class) || type.equals(Short.class);
    }

    protected Object fromString(String str) {
        return Short.valueOf(str);
    }

}
