package com.thoughtworks.xstream.converters.basic;

public class DoubleConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(double.class) || type.equals(Double.class);
    }

    protected Object fromString(String str) {
        return Double.valueOf(str);
    }

}
