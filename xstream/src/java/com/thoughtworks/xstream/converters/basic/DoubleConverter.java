package com.thoughtworks.xstream.converters.basic;

public class DoubleConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Double.valueOf(str);
    }

}
