package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.text.ParseException;
import java.util.Date;

/**
 * Converts a java.util.Date to a String as a date format,
 * retaining precision down to milliseconds.
 *
 * @author Joe Walnes
 */
public class DateConverter extends AbstractBasicConverter {

    private ThreadSafeSimpleDateFormat[] formats = {
        new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss.S a", 4, 20),
        new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ssa", 2, 20)
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
