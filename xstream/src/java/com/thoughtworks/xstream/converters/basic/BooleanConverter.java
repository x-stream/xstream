package com.thoughtworks.xstream.converters.basic;

public class BooleanConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return str.equals("true") ? Boolean.TRUE : Boolean.FALSE;
    }

}
