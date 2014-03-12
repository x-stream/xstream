/*
 * Copyright (C) 2008, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

import java.util.Iterator;
import java.util.Map;


/**
 * A key to a persistent storage and vice-versa strategy interface.
 * 
 * @author Guilherme Silveira
 * @since 1.3.1
 */
public interface PersistenceStrategy<K,V> {

    Iterator<Map.Entry<K, V>> iterator();

    int size();

    V get(Object key);

    V put(K key, V value);

    V remove(Object key);

}
