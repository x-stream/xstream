package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

import java.sql.Timestamp;

public class SqlTimestampConverter extends AbstractBasicConverter {

    protected Object fromString(String str) {
        return Timestamp.valueOf(str);
    }

    public boolean canConvert(Class type) {
        return type.equals(Timestamp.class);
    }

}
