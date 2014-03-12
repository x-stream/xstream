/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007, 2008, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. June 2006 by Guilherme Silveira
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
