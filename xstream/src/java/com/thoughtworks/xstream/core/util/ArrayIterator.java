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

import java.lang.reflect.Array;
import java.util.Iterator;


/**
 * Iterator for an array of arbitrary type.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class ArrayIterator implements Iterator<Object> {
    private final Object array;
    private int idx;
    private final int length;

    public ArrayIterator(final Object array) {
        this.array = array;
        length = Array.getLength(array);
    }

    @Override
    public boolean hasNext() {
        return idx < length;
    }

    @Override
    public Object next() {
        return Array.get(array, idx++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove from array");
    }
}
