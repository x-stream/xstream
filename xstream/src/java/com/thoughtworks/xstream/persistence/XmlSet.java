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
