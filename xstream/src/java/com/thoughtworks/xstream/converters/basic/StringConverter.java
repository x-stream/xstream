package com.thoughtworks.xstream.converters.basic;

public class StringConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return str;
    }

}
