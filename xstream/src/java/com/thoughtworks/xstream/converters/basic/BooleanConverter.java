package com.thoughtworks.xstream.converters.basic;

public class BooleanConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    protected Object fromString(String str) {
        return str.equals("true") ? Boolean.TRUE : Boolean.FALSE;
    }

}
