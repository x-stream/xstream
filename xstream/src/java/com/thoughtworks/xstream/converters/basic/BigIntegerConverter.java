package com.thoughtworks.xstream.converters.basic;

import java.math.BigInteger;

public class BigIntegerConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(BigInteger.class);
    }

    protected Object fromString(String str) {
        return new BigInteger(str);
    }

}
