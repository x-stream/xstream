/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2013, 2014, 2015, 2018, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.io.binary;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.security.InputManipulationException;


/**
 * A HierarchicalStreamReader that reads from a binary stream created by {@link BinaryStreamWriter}.
 * 
 * @author Joe Walnes
 * @see BinaryStreamReader
 * @since 1.2
 */
public class BinaryStreamReader implements ExtendedHierarchicalStreamReader {

    private final DataInputStream in;
    private final ReaderDepthState depthState = new ReaderDepthState();
    private final IdRegistry idRegistry = new IdRegistry();

    private Token pushback;
    private final Token.Formatter tokenFormatter = new Token.Formatter();

    public BinaryStreamReader(final InputStream inputStream) {
        in = new DataInputStream(inputStream);
        moveDown();
    }

    @Override
    public boolean hasMoreChildren() {
        return depthState.hasMoreChildren();
    }

    @Override
    public String getNodeName() {
        return depthState.getName();
    }

    @Override
    public String getValue() {
        return depthState.getValue();
    }

    @Override
    public String getAttribute(final String name) {
        return depthState.getAttribute(name);
    }

    @Override
    public String getAttribute(final int index) {
        return depthState.getAttribute(index);
    }

    @Override
    public int getAttributeCount() {
        return depthState.getAttributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return depthState.getAttributeName(index);
    }

    @Override
    public Iterator<String> getAttributeNames() {
        return depthState.getAttributeNames();
    }

    @Override
    public void moveDown() {
        depthState.push();
        final Token firstToken = readToken();
        switch (firstToken.getType()) {
        case Token.TYPE_START_NODE:
            depthState.setName(idRegistry.get(firstToken.getId()));
            break;
        default:
            throw new StreamException("Expected StartNode");
        }
        while (true) {
            final Token nextToken = readToken();
            switch (nextToken.getType()) {
            case Token.TYPE_ATTRIBUTE:
                depthState.addAttribute(idRegistry.get(nextToken.getId()), nextToken.getValue());
                break;
            case Token.TYPE_VALUE:
                depthState.setValue(nextToken.getValue());
                break;
            case Token.TYPE_END_NODE:
                depthState.setHasMoreChildren(false);
                pushBack(nextToken);
                return;
            case Token.TYPE_START_NODE:
                depthState.setHasMoreChildren(true);
                pushBack(nextToken);
                return;
            default:
                throw new StreamException("Unexpected token " + nextToken);
            }
        }
    }

    @Override
    public void moveUp() {
        depthState.pop();
        // We're done with this depth. Skip over all tokens until we get to the end.
        int depth = 0;
        slurp:
        while (true) {
            final Token nextToken = readToken();
            switch (nextToken.getType()) {
            case Token.TYPE_END_NODE:
                if (depth == 0) {
                    break slurp;
                } else {
                    depth--;
                }
                break;
            case Token.TYPE_START_NODE:
                depth++;
                break;
            default:
                // Ignore other tokens
            }
        }
        // Peek ahead to determine if there are any more kids at this level.
        final Token nextToken = readToken();
        switch (nextToken.getType()) {
        case Token.TYPE_END_NODE:
            depthState.setHasMoreChildren(false);
            break;
        case Token.TYPE_START_NODE:
            depthState.setHasMoreChildren(true);
            break;
        default:
            throw new StreamException("Unexpected token " + nextToken);
        }
        pushBack(nextToken);
    }

    @Override
    public int getLevel() {
        return depthState.getLevel();
    }

    private Token readToken() {
        if (pushback == null) {
            try {
                boolean mapping = false;
                do {
                    final Token token = tokenFormatter.read(in);
                    switch (token.getType()) {
                    case Token.TYPE_MAP_ID_TO_VALUE:
                        idRegistry.put(token.getId(), token.getValue());
                        mapping ^= true;
                        continue; // Next one please.
                    default:
                        return token;
                    }
                } while (mapping);
                throw new InputManipulationException("Binary stream will never have two mapping tokens in sequence");
            } catch (final IOException e) {
                throw new StreamException(e);
            }
        } else {
            final Token result = pushback;
            pushback = null;
            return result;
        }
    }

    public void pushBack(final Token token) {
        if (pushback == null) {
            pushback = token;
        } else {
            // If this happens, I've messed up :( -joe.
            throw new Error("Cannot push more than one token back");
        }
    }

    @Override
    public void close() {
        try {
            in.close();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public String peekNextChild() {
        if (depthState.hasMoreChildren()) {
            return idRegistry.get(pushback.getId());
        }
        return null;
    }

    @Override
    public HierarchicalStreamReader underlyingReader() {
        return this;
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        // TODO: When things go bad, it would be good to know where!
    }

    private static class IdRegistry {

        private final Map<Long, String> map = new HashMap<>();

        public void put(final long id, final String value) {
            map.put(Long.valueOf(id), value);
        }

        public String get(final long id) {
            final String result = map.get(Long.valueOf(id));
            if (result == null) {
                throw new StreamException("Unknown ID : " + id);
            } else {
                return result;
            }
        }
    }
}
