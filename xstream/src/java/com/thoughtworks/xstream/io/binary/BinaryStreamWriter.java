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

package com.thoughtworks.xstream.io.binary;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;


/**
 * @since 1.2
 */
public class BinaryStreamWriter implements ExtendedHierarchicalStreamWriter {

    private final IdRegistry idRegistry = new IdRegistry();
    private final DataOutputStream out;
    private final Token.Formatter tokenFormatter = new Token.Formatter();

    public BinaryStreamWriter(final OutputStream outputStream) {
        out = new DataOutputStream(outputStream);
    }

    @Override
    public void startNode(final String name) {
        write(new Token.StartNode(idRegistry.getId(name)));
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        startNode(name);
    }

    @Override
    public void addAttribute(final String name, final String value) {
        write(new Token.Attribute(idRegistry.getId(name), value));
    }

    @Override
    public void setValue(final String text) {
        write(new Token.Value(text));
    }

    @Override
    public void endNode() {
        write(new Token.EndNode());
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void close() {
        try {
            out.close();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    private void write(final Token token) {
        try {
            tokenFormatter.write(out, token);
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    private class IdRegistry {

        private long nextId = 0;
        private final Map<String, Long> ids = new HashMap<>();

        public long getId(final String value) {
            Long id = ids.get(value);
            if (id == null) {
                id = Long.valueOf(++nextId);
                ids.put(value, id);
                write(new Token.MapIdToValue(id.longValue(), value));
            }
            return id.longValue();
        }

    }
}
