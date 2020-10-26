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

package com.thoughtworks.xstream.io;

import java.util.Iterator;


/**
 * Provide an iterator over the attribute names of the current node of a reader.
 * 
 * @author Joe Walnes
 * @deprecated As of 1.4.8, it is an internal helper class only
 */
@Deprecated
public class AttributeNameIterator implements Iterator<String> {

    private int current;
    private final int count;
    private final HierarchicalStreamReader reader;

    public AttributeNameIterator(final HierarchicalStreamReader reader) {
        this.reader = reader;
        count = reader.getAttributeCount();
    }

    @Override
    public boolean hasNext() {
        return current < count;
    }

    @Override
    public String next() {
        return reader.getAttributeName(current++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
