/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 * List that allows items to be added with a priority that will affect the order in which they are later iterated over.
 * Objects with a high priority will appear before objects with a low priority in the list. If two objects of the same
 * priority are added to the list, the most recently added one will be iterated over first. Implementation uses a
 * TreeSet, which has a guaranteed add time of O(log(n)).
 * 
 * @author Joe Walnes
 * @author Guilherme Silveira
 */
public class PrioritizedList<E> implements Iterable<E> {

    private final Set<PrioritizedItem<E>> set = new TreeSet<>();
    private int lowestPriority = Integer.MAX_VALUE;
    private int lastId = 0;

    public void add(final E item, final int priority) {
        if (this.lowestPriority > priority) {
            this.lowestPriority = priority;
        }
        this.set.add(new PrioritizedItem<>(item, priority, ++lastId));
    }

    @Override
    public Iterator<E> iterator() {
        return new PrioritizedItemIterator<>(this.set.iterator());
    }

    private static class PrioritizedItem<V> implements Comparable<PrioritizedItem<V>> {

        final V value;
        final int priority;
        final int id;

        public PrioritizedItem(final V value, final int priority, final int id) {
            this.value = value;
            this.priority = priority;
            this.id = id;
        }

        @Override
        public int compareTo(final PrioritizedItem<V> other) {
            if (this.priority != other.priority) {
                return other.priority - this.priority;
            }
            return other.id - this.id;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(id).hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof PrioritizedItem)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            final PrioritizedItem<V> other = (PrioritizedItem<V>)obj;
            return this.id == other.id;
        }

    }

    private static class PrioritizedItemIterator<V> implements Iterator<V> {

        private final Iterator<PrioritizedItem<V>> iterator;

        public PrioritizedItemIterator(final Iterator<PrioritizedItem<V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next().value;
        }

    }

}
