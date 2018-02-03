/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.io.binary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Maintains the state of a pull reader at various states in the document depth. Used by the {@link BinaryStreamReader}
 * 
 * @author Joe Walnes
 * @since 1.2
 */
class ReaderDepthState {

    private static final String EMPTY_STRING = "";

    private static class State {
        String name;
        String value;
        List<Attribute> attributes;
        boolean hasMoreChildren;
        State parent;
        int level;
    }

    private static class Attribute {
        String name;
        String value;
    }

    private State current;

    public void push() {
        final State newState = new State();
        newState.parent = current;
        newState.level = getLevel() + 1;
        current = newState;
    }

    public void pop() {
        current = current.parent;
    }

    public int getLevel() {
        return current != null ? current.level : 0;
    }

    public String getName() {
        return current.name;
    }

    public void setName(final String name) {
        current.name = name;
    }

    public String getValue() {
        return current.value == null ? EMPTY_STRING : current.value;
    }

    public void setValue(final String value) {
        current.value = value;
    }

    public boolean hasMoreChildren() {
        return current.hasMoreChildren;
    }

    public void setHasMoreChildren(final boolean hasMoreChildren) {
        current.hasMoreChildren = hasMoreChildren;
    }

    public void addAttribute(final String name, final String value) {
        final Attribute attribute = new Attribute();
        attribute.name = name;
        attribute.value = value;
        if (current.attributes == null) {
            current.attributes = new ArrayList<>();
        }
        current.attributes.add(attribute);
    }

    public String getAttribute(final String name) {
        if (current.attributes == null) {
            return null;
        } else {
            // For short maps, it's faster to iterate then do a hashlookup.
            for (final Attribute attribute : current.attributes) {
                if (attribute.name.equals(name)) {
                    return attribute.value;
                }
            }
            return null;
        }
    }

    public String getAttribute(final int index) {
        if (current.attributes == null) {
            return null;
        } else {
            final Attribute attribute = current.attributes.get(index);
            return attribute.value;
        }
    }

    public String getAttributeName(final int index) {
        if (current.attributes == null) {
            return null;
        } else {
            final Attribute attribute = current.attributes.get(index);
            return attribute.name;
        }
    }

    public int getAttributeCount() {
        return current.attributes == null ? 0 : current.attributes.size();
    }

    public Iterator<String> getAttributeNames() {
        if (current.attributes == null) {
            return Collections.<String>emptyIterator();
        } else {
            final Iterator<Attribute> attributeIterator = current.attributes.iterator();
            return new Iterator<String>() {
                @Override
                public boolean hasNext() {
                    return attributeIterator.hasNext();
                }

                @Override
                public String next() {
                    final Attribute attribute = attributeIterator.next();
                    return attribute.name;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

}
