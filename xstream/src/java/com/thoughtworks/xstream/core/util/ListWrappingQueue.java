/*
 * Copyright (C) 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. Mai 2020 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * A Queue wrapper for a list
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public final class ListWrappingQueue<E> implements Queue<E> {
    private final List<E> list;

    /**
     * Constructs a QueueImplementation.
     * 
     * @param list
     * @since upcoming
     */
    public ListWrappingQueue(final List<E> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean remove(final Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean add(final E e) {
        return this.list.add(e);
    }

    @Override
    public boolean offer(final E e) {
        return this.list.add(e);
    }

    @Override
    public E remove() {
        return this.list.remove(0);
    }

    @Override
    public E poll() {
        return this.list.remove(0);
    }

    @Override
    public E element() {
        return this.list.get(0);
    }

    @Override
    public E peek() {
        return this.list.get(0);
    }
}