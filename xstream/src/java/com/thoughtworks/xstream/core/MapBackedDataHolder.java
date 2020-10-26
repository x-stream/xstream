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

package com.thoughtworks.xstream.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.DataHolder;


public class MapBackedDataHolder implements DataHolder {
    private final Map<Object, Object> map;

    public MapBackedDataHolder() {
        this(new HashMap<>());
    }

    public MapBackedDataHolder(final Map<Object, Object> map) {
        this.map = map;
    }

    @Override
    public Object get(final Object key) {
        return map.get(key);
    }

    @Override
    public void put(final Object key, final Object value) {
        map.put(key, value);
    }

    @Override
    public Iterator<Object> keys() {
        return Collections.unmodifiableCollection(map.keySet()).iterator();
    }
}
