/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
