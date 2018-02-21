/*
 * Copyright (c) 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. May 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.Arrays;


/**
 * A simple pool implementation.
 * 
 * @author J&ouml;rg Schaible
 * @author Joe Walnes
 */
public class Pool<T> {

    public interface Factory<T> {
        public T newInstance();
    }

    private final int initialPoolSize;
    private final int maxPoolSize;
    private final Factory<T> factory;
    private transient T[] pool;
    private transient int nextAvailable;

    public Pool(final int initialPoolSize, final int maxPoolSize, final Factory<T> factory) {
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.factory = factory;
    }

    @SafeVarargs
    private final T[] newArray(final int capacity, final T... t) {
        return Arrays.copyOf(t, capacity);
    }

    public T fetchFromPool() {
        T result;
        synchronized (this) {
            if (pool == null) {
                final T[] all = newArray(maxPoolSize);
                pool = all;
                for (nextAvailable = initialPoolSize; nextAvailable > 0;) {
                    putInPool(factory.newInstance());
                }
            }
            while (nextAvailable == maxPoolSize) {
                try {
                    wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Interrupted whilst waiting for a free item in the pool: "
                        + e.getMessage());
                }
            }
            result = pool[nextAvailable++];
            if (result == null) {
                result = factory.newInstance();
                putInPool(result);
                ++nextAvailable;
            }
        }
        return result;
    }

    protected void putInPool(final T object) {
        synchronized (this) {
            if (nextAvailable == 0) {
                throw new IllegalStateException("Cannot put more objects than "
                    + maxPoolSize
                    + " elements into this pool");
            }
            pool[--nextAvailable] = object;
            if (object == null) {
                for (int i = maxPoolSize; i > nextAvailable;) {
                    if (pool[--i] != null) {
                        pool[nextAvailable] = pool[i];
                        pool[i] = null;
                        break;
                    }
                }
            }

            notify();
        }
    }
}
