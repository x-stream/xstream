package com.thoughtworks.xstream.converters.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadSafeSimpleDateFormat {

    private String formatString;
    private DateFormat[] pool;
    private int nextAvailable = 0;

    public ThreadSafeSimpleDateFormat(String format, int initialPoolSize, int maxPoolSize) {
        this.formatString = format;
        nextAvailable = -1;
        pool = new DateFormat[maxPoolSize];
        for (int i = 0; i < initialPoolSize; i++) {
            putInPool(createNew());
        }
    }

    public String format(Date date) {
        DateFormat format = fetchFromPool();
        try {
            return format.format(date);
        } finally {
            putInPool(format);
        }
    }

    public Date parse(String date) throws ParseException {
        DateFormat format = fetchFromPool();
        try {
            return format.parse(date);
        } finally {
            putInPool(format);
        }
    }

    private DateFormat fetchFromPool() {
        DateFormat result;
        synchronized (pool) {
            while (nextAvailable < 0) {
                try {
                    pool.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted whilst waiting " +
                            "for a free item in the pool : " + e.getMessage());
                }
            }
            result = pool[nextAvailable];
            nextAvailable--;
        }
        if (result == null) {
            result = createNew();
            putInPool(result);
        }
        return result;
    }

    private void putInPool(DateFormat format) {
        synchronized (pool) {
            nextAvailable++;
            pool[nextAvailable] = format;
            pool.notify();
        }
    }

    private DateFormat createNew() {
        return new SimpleDateFormat(formatString);
    }

}
