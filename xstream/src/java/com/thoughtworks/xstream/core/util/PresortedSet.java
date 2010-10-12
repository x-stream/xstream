/*
 * Copyright (C) 2006, 2007, 2010 XStream Committers.
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
public class PresortedSet implements SortedSet {
    private final List list = new ArrayList();
    private final Comparator comparator;

    public PresortedSet() {
        this(null);
    }

    public PresortedSet(Comparator comparator) {
        this(comparator, null);
    }

    public PresortedSet(Comparator comparator, Collection c) {
        this.comparator = comparator;
        if (c != null) {
            addAll(c);
        }
    }

    public boolean add(Object e) {
        return this.list.add(e);
    }

    public boolean addAll(Collection c) {
        return this.list.addAll(c);
    }

    public void clear() {
        this.list.clear();
    }

    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    public boolean containsAll(Collection c) {
        return this.list.containsAll(c);
    }

    public boolean equals(Object o) {
        return this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public Iterator iterator() {
        return this.list.iterator();
    }

    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    public boolean removeAll(Collection c) {
        return this.list.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return this.list.retainAll(c);
    }

    public int size() {
        return this.list.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return this.list.toArray();
    }

    public Object[] toArray(Object[] a) {
        return this.list.toArray(a);
    }

    public Comparator comparator() {
        return comparator;
    }

    public Object first() {
        return list.isEmpty() ? null : list.get(0);
    }

    public SortedSet headSet(Object toElement) {
        throw new UnsupportedOperationException();
    }

    public Object last() {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public SortedSet subSet(Object fromElement, Object toElement) {
        throw new UnsupportedOperationException();
    }

    public SortedSet tailSet(Object fromElement) {
        throw new UnsupportedOperationException();
    }
}