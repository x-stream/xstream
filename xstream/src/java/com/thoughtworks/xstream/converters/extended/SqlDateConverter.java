package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

import java.sql.Date;
import java.sql.Time;

/**
 * Converts a java.sql.Date to text.
 *
 * @author Jose A. Illescas 
 */
public class SqlDateConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    protected Object fromString(String str) {
        return Date.valueOf(str);
    }

}
