package com.thoughtworks.xstream.converters.basic;

public class ByteConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return new Byte((byte) Integer.parseInt(str));
    }

}
