/*
 * Copyright (c) 2007 XStream Committers.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of XStream nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package com.thoughtworks.xstream.core.util;

import java.text.DateFormat;

/**
 * A simple pool implementation.
 *
 * @author J&ouml;rg Schaible
 * @author Joe Walnes
 */
public class Pool {
    
    public interface Factory {
        public Object newInstance();
    }

    private final int initialPoolSize;
    private final int maxPoolSize;
    private final Factory factory;
    private transient Object[] pool;
    private transient int nextAvailable;
    private transient Object mutex = new Object();

    public Pool(int initialPoolSize, int maxPoolSize, Factory factory) {
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.factory = factory;
    }

    public Object fetchFromPool() {
        Object result;
        synchronized (mutex) {
            if (pool == null) {
                pool = new DateFormat[maxPoolSize];
                for (nextAvailable = initialPoolSize; nextAvailable > 0; ) {
                    putInPool(factory.newInstance());
                }
            }
            while (nextAvailable == maxPoolSize) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted whilst waiting " +
                            "for a free item in the pool : " + e.getMessage());
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

    protected void putInPool(Object object) {
        synchronized (mutex) {
            pool[--nextAvailable] = object;
            mutex.notify();
        }
    }
    
    private Object readResolve() {
        mutex = new Object();
        return this;
    }
}
