/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Base class that contains common functionality across HierarchicalStreamReader implementations that need to read from
 * a pull parser.
 *
 * @author Joe Walnes
 * @author James Strachan
 */
public abstract class AbstractPullReader extends AbstractXmlReader {

    protected static final int START_NODE = 1;
    protected static final int END_NODE = 2;
    protected static final int TEXT = 3;
    protected static final int COMMENT = 4;
    protected static final int OTHER = 0;

    private final FastStack<String> elementStack = new FastStack<>(16);
    private final FastStack<Event> pool = new FastStack<>(16);

    private final FastStack<Event> lookahead = new FastStack<>(4);
    private final FastStack<Event> lookback = new FastStack<>(4);
    private boolean marked;

    private static class Event {
        int type;
        String value;
    }

    /**
     * @since 1.4
     */
    protected AbstractPullReader(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link AbstractPullReader#AbstractPullReader(NameCoder)} instead
     */
    @Deprecated
    protected AbstractPullReader(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    /**
     * Pull the next event from the stream.
     * <p>
     * This MUST return {@link #START_NODE}, {@link #END_NODE}, {@link #TEXT}, {@link #COMMENT}, {@link #OTHER} or throw
     * {@link com.thoughtworks.xstream.io.StreamException}.
     * </p>
     * <p>
     * The underlying pull parser will most likely return its own event types. These must be mapped to the appropriate
     * events.
     * </p>
     */
    protected abstract int pullNextEvent();

    /**
     * Pull the name of the current element from the stream.
     */
    protected abstract String pullElementName();

    /**
     * Pull the contents of the current text node from the stream.
     */
    protected abstract String pullText();

    @Override
    public boolean hasMoreChildren() {
        mark();
        while (true) {
            switch (readEvent().type) {
            case START_NODE:
                reset();
                return true;
            case END_NODE:
                reset();
                return false;
            default:
                continue;
            }
        }
    }

    @Override
    public void moveDown() {
        final int currentDepth = elementStack.size();
        while (elementStack.size() <= currentDepth) {
            move();
            if (elementStack.size() < currentDepth) {
                throw new RuntimeException(); // sanity check
            }
        }
    }

    @Override
    public void moveUp() {
        final int currentDepth = elementStack.size();
        while (elementStack.size() >= currentDepth) {
            move();
        }
    }

    @Override
    public int getLevel() {
        return elementStack.size();
    }

    private void move() {
        final Event event = readEvent();
        pool.push(event);
        switch (event.type) {
        case START_NODE:
            elementStack.push(pullElementName());
            break;
        case END_NODE:
            elementStack.pop();
            break;
        }
    }

    private Event readEvent() {
        if (marked) {
            if (lookback.hasStuff()) {
                return lookahead.push(lookback.pop());
            } else {
                return lookahead.push(readRealEvent());
            }
        } else {
            if (lookback.hasStuff()) {
                return lookback.pop();
            } else {
                return readRealEvent();
            }
        }
    }

    private Event readRealEvent() {
        final Event event = pool.hasStuff() ? (Event)pool.pop() : new Event();
        event.type = pullNextEvent();
        if (event.type == TEXT) {
            event.value = pullText();
        } else if (event.type == START_NODE) {
            event.value = pullElementName();
        } else {
            event.value = null;
        }
        return event;
    }

    public void mark() {
        marked = true;
    }

    public void reset() {
        while (lookahead.hasStuff()) {
            lookback.push(lookahead.pop());
        }
        marked = false;
    }

    @Override
    public String getValue() {
        // we should collapse together any text which
        // contains comments

        // lets only use a string buffer when we get 2 strings
        // to avoid copying strings
        String last = null;
        StringBuffer buffer = null;

        mark();
        Event event = readEvent();
        while (true) {
            if (event.type == TEXT) {
                final String text = event.value;
                if (text != null && text.length() > 0) {
                    if (last == null) {
                        last = text;
                    } else {
                        if (buffer == null) {
                            buffer = new StringBuffer(last);
                        }
                        buffer.append(text);
                    }
                }
            } else if (event.type != COMMENT) {
                break;
            }
            event = readEvent();
        }
        reset();
        if (buffer != null) {
            return buffer.toString();
        } else {
            return last == null ? "" : last;
        }
    }

    @Override
    public String getNodeName() {
        return unescapeXmlName(elementStack.peek());
    }

    @Override
    public String peekNextChild() {
        mark();
        while (true) {
            final Event ev = readEvent();
            switch (ev.type) {
            case START_NODE:
                reset();
                return ev.value;
            case END_NODE:
                reset();
                return null;
            default:
                continue;
            }
        }
    }
}
