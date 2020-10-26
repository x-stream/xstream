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

package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.naming.NameCoder;


public abstract class AbstractDocumentReader extends AbstractXmlReader implements DocumentReader {

    private final FastStack<Pointer> pointers = new FastStack<>(16);
    private Object current;

    protected AbstractDocumentReader(final Object rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    protected AbstractDocumentReader(final Object rootElement, final NameCoder nameCoder) {
        super(nameCoder);
        current = rootElement;
        pointers.push(new Pointer());
        reassignCurrentElement(current);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link AbstractDocumentReader#AbstractDocumentReader(Object, NameCoder)} instead.
     */
    @Deprecated
    protected AbstractDocumentReader(final Object rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    protected abstract void reassignCurrentElement(Object current);

    protected abstract Object getParent();

    protected abstract Object getChild(int index);

    protected abstract int getChildCount();

    private static class Pointer {
        public int v;
    }

    @Override
    public boolean hasMoreChildren() {
        final Pointer pointer = pointers.peek();

        if (pointer.v < getChildCount()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void moveUp() {
        current = getParent();
        pointers.popSilently();
        reassignCurrentElement(current);
    }

    @Override
    public void moveDown() {
        final Pointer pointer = pointers.peek();
        pointers.push(new Pointer());

        current = getChild(pointer.v);

        pointer.v++;
        reassignCurrentElement(current);
    }

    @Override
    public int getLevel() {
        return pointers.size();
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
    }

    @Override
    public Object getCurrent() {
        return current;
    }

    @Override
    public void close() {
        // don't need to do anything
    }
}
