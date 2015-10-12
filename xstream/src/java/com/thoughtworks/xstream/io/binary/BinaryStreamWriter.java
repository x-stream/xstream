/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Joe Walnes
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
