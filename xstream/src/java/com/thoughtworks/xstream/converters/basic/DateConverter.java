package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateConverter extends AbstractBasicConverter {

    private static final DateFormat[] formats = {
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S a"),
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ssa")
    };

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    protected Object fromString(String str) {
        for (int i = 0; i < formats.length; i++) {
            try {
                return formats[i].parse(str);
            } catch (ParseException e) {
                // no worries, let's try the next format.
            }
        }
        // no formats left to try
        throw new ConversionException("Cannot parse date " + str);
    }

    protected String toString(Object obj) {
        Date date = (Date) obj;
        return formats[0].format(date);
    }

}
