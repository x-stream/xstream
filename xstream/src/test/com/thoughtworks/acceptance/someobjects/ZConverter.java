package com.thoughtworks.acceptance.someobjects;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class ZConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Z.class);
    }

    public Object fromString(String str) {
        return new Z("z");
    }

}
