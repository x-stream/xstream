package com.thoughtworks.xstream.converters.basic;

public class ShortConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Short.valueOf(str);
    }

}
