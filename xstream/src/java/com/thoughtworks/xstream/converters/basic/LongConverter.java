package com.thoughtworks.xstream.converters.basic;

public class LongConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    protected Object fromString(String str) {
        return Long.valueOf(str);
    }

}
