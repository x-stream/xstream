package com.thoughtworks.xstream.converters.extended;

import java.sql.Date;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.sql.Date to text.
 *
 * @author Jose A. Illescas 
 */
public class SqlDateConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    public Object fromString(String str) {
        return Date.valueOf(str);
    }

}
