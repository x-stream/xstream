package com.thoughtworks.xstream.converters.basic;

import java.text.ParseException;
import java.util.Date;

/**
 * Wrapper around java.text.SimpleDateFormat that can
 * be called by multiple threads concurrently.
 *
 * @author Joe Walnes
 * @deprecated since 1.2.1, moved to com.thoughtworks.xstream.core.util. 
 * It is not part of public API, use on your own risk.
 */
public class ThreadSafeSimpleDateFormat extends com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat {

    public ThreadSafeSimpleDateFormat(String format, int initialPoolSize, int maxPoolSize) {
        super(format, initialPoolSize, maxPoolSize);
    }

    public String format(Date date) {
        return super.format(date);
    }

    public Date parse(String date) throws ParseException {
        return super.parse(date);
    }
}
