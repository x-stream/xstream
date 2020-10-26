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

/**
 * Base class to make it easy to create wrappers (decorators) for HierarchicalStreamWriter.
 * 
 * @author Joe Walnes
 */
public abstract class WriterWrapper implements ExtendedHierarchicalStreamWriter {

    protected HierarchicalStreamWriter wrapped;

    protected WriterWrapper(final HierarchicalStreamWriter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void startNode(final String name) {
        wrapped.startNode(name);
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        wrapped.startNode(name, clazz);
    }

    @Override
    public void endNode() {
        wrapped.endNode();
    }

    @Override
    public void addAttribute(final String key, final String value) {
        wrapped.addAttribute(key, value);
    }

    @Override
    public void setValue(final String text) {
        wrapped.setValue(text);
    }

    @Override
    public void flush() {
        wrapped.flush();
    }

    @Override
    public void close() {
        wrapped.close();
    }

    @Override
    public HierarchicalStreamWriter underlyingWriter() {
        return wrapped.underlyingWriter();
    }

}
