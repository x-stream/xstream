package com.thoughtworks.xstream.converters.basic;

public class IntConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    protected Object fromString(String str) {
        return Integer.valueOf(str);
    }

}
