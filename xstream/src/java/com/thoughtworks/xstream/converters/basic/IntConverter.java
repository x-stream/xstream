package com.thoughtworks.xstream.converters.basic;

public class IntConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Integer.valueOf(str);
    }

}
