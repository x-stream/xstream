package com.thoughtworks.xstream.converters.basic;

public class FloatConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Float.valueOf(str);
    }

}
