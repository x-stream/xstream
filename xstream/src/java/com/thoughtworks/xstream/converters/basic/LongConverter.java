package com.thoughtworks.xstream.converters.basic;

public class LongConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Long.valueOf(str);
    }

}
