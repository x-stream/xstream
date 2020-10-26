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

import com.thoughtworks.xstream.converters.ErrorWriter;


/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamReader.
 * 
 * @author Joe Walnes
 */
public abstract class ReaderWrapper implements ExtendedHierarchicalStreamReader {

    protected HierarchicalStreamReader wrapped;

    protected ReaderWrapper(final HierarchicalStreamReader reader) {
        wrapped = reader;
    }

    @Override
    public boolean hasMoreChildren() {
        return wrapped.hasMoreChildren();
    }

    @Override
    public void moveDown() {
        wrapped.moveDown();
    }

    @Override
    public void moveUp() {
        wrapped.moveUp();
    }

    @Override
    public int getLevel() {
        return wrapped.getLevel();
    }

    @Override
    public String getNodeName() {
        return wrapped.getNodeName();
    }

    @Override
    public String getValue() {
        return wrapped.getValue();
    }

    @Override
    public String getAttribute(final String name) {
        return wrapped.getAttribute(name);
    }

    @Override
    public String getAttribute(final int index) {
        return wrapped.getAttribute(index);
    }

    @Override
    public int getAttributeCount() {
        return wrapped.getAttributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return wrapped.getAttributeName(index);
    }

    @Override
    public Iterator<String> getAttributeNames() {
        return wrapped.getAttributeNames();
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        wrapped.appendErrors(errorWriter);
    }

    @Override
    public void close() {
        wrapped.close();
    }

    @Override
    public String peekNextChild() {
        return wrapped.peekNextChild();
    }

    @Override
    public HierarchicalStreamReader underlyingReader() {
        return wrapped.underlyingReader();
    }
}
