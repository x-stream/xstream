package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter extends AbstractBasicConverter {

    // DateFormats are not thread safe, so ensure we only ever have one
    // SimpleDateFormat per thread.
    private static final ThreadLocal formats = new ThreadLocal() {
        protected Object initialValue() {
            return new DateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S a"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ssa")
            };
        }
    };

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    protected Object fromString(String str) {
        DateFormat[] dateFormats = ((DateFormat[]) formats.get());
        for (int i = 0; i < dateFormats.length; i++) {
            try {
                return dateFormats[i].parse(str);
            } catch (ParseException e) {
                // no worries, let's try the next format.
            }
        }
        // no formats left to try
        throw new ConversionException("Cannot parse date " + str);
    }

    protected String toString(Object obj) {
        Date date = (Date) obj;
        return ((DateFormat[]) formats.get())[0].format(date);
    }

}
