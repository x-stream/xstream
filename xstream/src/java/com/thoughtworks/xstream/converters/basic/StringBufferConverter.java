package com.thoughtworks.xstream.converters.basic;

public class StringBufferConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return new StringBuffer(str);
    }

    public boolean canConvert(Class type) {
        return type.equals(StringBuffer.class);
    }
}
