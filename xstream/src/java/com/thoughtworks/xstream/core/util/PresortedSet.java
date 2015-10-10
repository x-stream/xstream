/*
 * Copyright (C) 2006, 2007, 2010, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * Created on 12.10.2010 by Joerg Schaible, extracted from TreeSetConverter.
 */
package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;


/**
 * @author J&ouml;rg Schaible
 */
public class PresortedSet<E> implements SortedSet<E> {
    private final List<E> list = new ArrayList<>();
    private final Comparator<E> comparator;

    public PresortedSet() {
        this(null);
    }

    public PresortedSet(final Comparator<E> comparator) {
        this(comparator, null);
    }

    public PresortedSet(final Comparator<E> comparator, final Collection<E> c) {
        this.comparator = comparator;
        if (c != null) {
            addAll(c);
        }
    }

    @Override
    public boolean add(final E e) {
        return this.list.add(e);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return this.list.addAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return this.list.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return this.list.equals(o);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @Override
    public boolean remove(final Object o) {
        return this.list.remove(o);
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
    public int size() {
        return this.list.size();
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
    public Comparator<E> comparator() {
        return comparator;
    }

    @Override
    public E first() {
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SortedSet<E> headSet(final Object toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E last() {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    @Override
    public SortedSet<E> subSet(final Object fromElement, final Object toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<E> tailSet(final Object fromElement) {
        throw new UnsupportedOperationException();
    }
}
