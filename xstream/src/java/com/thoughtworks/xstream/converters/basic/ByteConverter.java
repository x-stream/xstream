package com.thoughtworks.xstream.converters.basic;

public class ByteConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(byte.class) || type.equals(Byte.class);
    }

    protected Object fromString(String str) {
        return new Byte((byte) Integer.parseInt(str));
    }

}
