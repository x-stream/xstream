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

package com.thoughtworks.xstream.persistence;

import java.util.AbstractList;


/**
 * A persistent list implementation backed on a XmlMap.
 * 
 * @author Guilherme Silveira
 */
public class XmlArrayList<V> extends AbstractList<V> {

    private final XmlMap<Integer, V> map;

    public XmlArrayList(final PersistenceStrategy<Integer, V> persistenceStrategy) {
        this.map = new XmlMap<>(persistenceStrategy);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public V set(final int index, final V element) {
        rangeCheck(index);
        final V value = get(index);
        map.put(Integer.valueOf(index), element);
        return value;
    }

    @Override
    public void add(final int index, final V element) {
        final int size = size();
        if (index >= size + 1 || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        final int to = index != size ? index - 1 : index;
        for (int i = size; i > to; i--) {
            map.put(Integer.valueOf(i + 1), map.get(Integer.valueOf(i)));
        }
        map.put(Integer.valueOf(index), element);
    }

    private void rangeCheck(final int index) {
        final int size = size();
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public V get(final int index) {
        rangeCheck(index);
        return map.get(Integer.valueOf(index));
    }

    @Override
    public V remove(final int index) {
        final int size = size();
        rangeCheck(index);
        final V value = map.get(Integer.valueOf(index));
        for (int i = index; i < size - 1; i++) {
            map.put(Integer.valueOf(i), map.get(Integer.valueOf(i + 1)));
        }
        map.remove(Integer.valueOf(size - 1));
        return value;
    }

}
