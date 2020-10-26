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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A persistent map. Its values are actually serialized as xml files.
 * <p>
 * If you need an application-wide synchronized version of this map, try the respective Collections methods.
 * </p>
 * 
 * @author Guilherme Silveira
 */
public class XmlMap<K, V> extends AbstractMap<K, V> {

    private final PersistenceStrategy<K, V> persistenceStrategy;

    public XmlMap(final PersistenceStrategy<K, V> streamStrategy) {
        this.persistenceStrategy = streamStrategy;
    }

    @Override
    public int size() {
        return persistenceStrategy.size();
    }

    @Override
    public V get(final Object key) {
        // faster lookup
        return persistenceStrategy.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return persistenceStrategy.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return persistenceStrategy.remove(key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new XmlMapEntries();
    }

    class XmlMapEntries extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public int size() {
            return XmlMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return XmlMap.this.isEmpty();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return persistenceStrategy.iterator();
        }

    }

}
