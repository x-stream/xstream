package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateConverter extends AbstractBasicConverter {

    private DateFormat dateFormat;

    public DateConverter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public DateConverter() {
        this(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssa"));
    }

    protected Object fromString(String str) {
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            throw new ConversionException("Cannot parse date " + str, e);
        }
    }

    protected String toString(Object obj) {
        Date date = (Date) obj;
        return dateFormat.format(date);
    }

}
