package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

public class ZConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(Z.class);
    }

    protected Object fromString(String str) {
        return new Z("z");
    }

}
