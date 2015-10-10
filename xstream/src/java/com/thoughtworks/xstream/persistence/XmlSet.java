/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007, 2008, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. June 2006 by Guilherme Silveira
 */
package com.thoughtworks.xstream.persistence;

import java.util.AbstractSet;
import java.util.Iterator;


/**
 * A persistent set implementation.
 * 
 * @author Guilherme Silveira
 */
public class XmlSet<V> extends AbstractSet<V> {

    private final XmlMap<Long, V> map;

    public XmlSet(final PersistenceStrategy<Long, V> persistenceStrategy) {
        this.map = new XmlMap<>(persistenceStrategy);
    }

    @Override
    public Iterator<V> iterator() {
        return map.values().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean add(final V o) {
        if (map.containsValue(o)) {
            return false;
        } else {
            // not-synchronized!
            map.put(findEmptyKey(), o);
            return true;
        }
    }

    private Long findEmptyKey() {
        long i = System.currentTimeMillis();
        while (map.containsKey(Long.valueOf(i))) {
            i++;
        }
        return Long.valueOf(i);
    }

}
