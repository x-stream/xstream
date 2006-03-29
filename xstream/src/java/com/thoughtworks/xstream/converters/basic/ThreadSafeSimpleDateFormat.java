package com.thoughtworks.xstream.converters.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Wrapper around java.text.SimpleDateFormat that can
 * be called by multiple threads concurrently.
 * <p/>
 * <p>SimpleDateFormat has a high overhead in creating
 * and is not thread safe. To make best use of resources,
 * the ThreadSafeSimpleDateFormat provides a dynamically
 * sizing pool of instances, each of which will only
 * be called by a single thread at a time.</p>
 * <p/>
 * <p>The pool has a maximum capacity, to limit overhead.
 * If all instances in the pool are in use and another is
 * required, it shall block until one becomes available.</p>
 *
 * @author Joe Walnes
 */
public class ThreadSafeSimpleDateFormat {

    private final String formatString;
    private final int initialPoolSize;
    private final int maxPoolSize;
    private transient DateFormat[] pool;
    private transient int nextAvailable;
    private transient Object mutex = new Object();

    public ThreadSafeSimpleDateFormat(String format, int initialPoolSize, int maxPoolSize) {
        this.formatString = format;
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
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
        synchronized (mutex) {
            if (pool == null) {
                nextAvailable = -1;
                pool = new DateFormat[maxPoolSize];
                for (int i = 0; i < initialPoolSize; i++) {
                    putInPool(createNew());
                }
            }
            while (nextAvailable < 0) {
                try {
                    mutex.wait();
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
        synchronized (mutex) {
            nextAvailable++;
            pool[nextAvailable] = format;
            mutex.notify();
        }
    }

    private DateFormat createNew() {
        return new SimpleDateFormat(formatString, Locale.ENGLISH);
    }
    
    private Object readResolve() {
        mutex = new Object();
        return this;
    }

}
