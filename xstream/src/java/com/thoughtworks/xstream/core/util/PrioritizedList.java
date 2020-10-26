/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
