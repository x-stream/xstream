/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 09. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Store IDs against given object references.
 * <p>
 * Behaves similar to java.util.IdentityHashMap, but in JDK1.3 as well. Additionally the implementation keeps track of
 * orphaned IDs by using a WeakReference to store the reference object.
 * </p>
 */
public class ObjectIdDictionary<E> {

    private final Map<? super Wrapper, E> map = new HashMap<>();
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    private static interface Wrapper {
        @Override
        int hashCode();

        @Override
        boolean equals(Object obj);

        @Override
        String toString();

        Object get();
    }

    private static class IdWrapper implements Wrapper {

        private final Object obj;
        private final int hashCode;

        public IdWrapper(final Object obj) {
            hashCode = System.identityHashCode(obj);
            this.obj = obj;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(final Object other) {
            return obj == ((Wrapper)other).get();
        }

        @Override
        public String toString() {
            return obj.toString();
        }

        @Override
        public Object get() {
            return obj;
        }
    }

    private class WeakIdWrapper extends WeakReference<Object> implements Wrapper {

        private final int hashCode;

        public WeakIdWrapper(final Object obj) {
            super(obj, queue);
            hashCode = System.identityHashCode(obj);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(final Object other) {
            return get() == ((Wrapper)other).get();
        }

        @Override
        public String toString() {
            final Object obj = get();
            return obj == null ? "(null)" : obj.toString();
        }
    }

    public void associateId(final Object obj, final E id) {
        map.put(new WeakIdWrapper(obj), id);
        cleanup();
    }

    public E lookupId(final Object obj) {
        final E id = map.get(new IdWrapper(obj));
        return id;
    }

    public boolean containsId(final Object item) {
        final boolean b = map.containsKey(new IdWrapper(item));
        return b;
    }

    public void removeId(final Object item) {
        map.remove(new IdWrapper(item));
        cleanup();
    }

    public int size() {
        cleanup();
        return map.size();
    }

    @SuppressWarnings("unchecked")
    private void cleanup() {
        WeakIdWrapper wrapper;
        while ((wrapper = (WeakIdWrapper)queue.poll()) != null) {
            map.remove(wrapper);
        }
    }
}
