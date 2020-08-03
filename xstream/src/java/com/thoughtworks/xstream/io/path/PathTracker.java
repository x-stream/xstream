/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.path;

import java.util.HashMap;
import java.util.Map;


/**
 * Maintains the current {@link Path} as a stream is moved through.
 * <p>
 * Can be linked to a {@link com.thoughtworks.xstream.io.HierarchicalStreamWriter} or
 * {@link com.thoughtworks.xstream.io.HierarchicalStreamReader} by wrapping them with a {@link PathTrackingWriter} or
 * {@link PathTrackingReader}.
 * </p>
 * <h3>Example</h3>
 * 
 * <pre>
 * PathTracker tracker = new PathTracker();
 * tracker.pushElement(&quot;table&quot;);
 * tracker.pushElement(&quot;tr&quot;);
 * tracker.pushElement(&quot;td&quot;);
 * tracker.pushElement(&quot;form&quot;);
 * tracker.popElement(&quot;form&quot;);
 * tracker.popElement(&quot;td&quot;);
 * tracker.pushElement(&quot;td&quot;);
 * tracker.pushElement(&quot;div&quot;);
 * 
 * Path path = tracker.getPath(); // returns &quot;/table/tr/td[2]/div&quot;
 * </pre>
 * 
 * @see Path
 * @see PathTrackingReader
 * @see PathTrackingWriter
 * @author Joe Walnes
 */
public class PathTracker {

    private int pointer;
    private int capacity;
    private String[] pathStack;
    private Map<String, Integer>[] indexMapStack;

    private Path currentPath;

    public PathTracker() {
        this(16);
    }

    /**
     * @param initialCapacity Size of the initial stack of nodes (one level per depth in the tree). Note that this is
     *            only for optimizations - the stack will resize itself if it exceeds its capacity. If in doubt, use the
     *            other constructor.
     */
    public PathTracker(final int initialCapacity) {
        capacity = Math.max(1, initialCapacity);
        pathStack = new String[capacity];
        @SuppressWarnings("unchecked")
        final Map<String, Integer>[] newIndexMapStack = new Map[capacity];
        indexMapStack = newIndexMapStack;
    }

    /**
     * Notify the tracker that the stream has moved into a new element.
     * 
     * @param name Name of the element
     */
    public void pushElement(final String name) {
        if (pointer + 1 >= capacity) {
            resizeStacks(capacity * 2);
        }
        pathStack[pointer] = name;
        Map<String, Integer> indexMap = indexMapStack[pointer];
        if (indexMap == null) {
            indexMap = new HashMap<>();
            indexMapStack[pointer] = indexMap;
        }
        if (indexMap.containsKey(name)) {
            indexMap.put(name, Integer.valueOf(indexMap.get(name).intValue() + 1));
        } else {
            indexMap.put(name, Integer.valueOf(1));
        }
        pointer++;
        currentPath = null;
    }

    /**
     * Notify the tracker that the stream has moved out of an element.
     */
    public void popElement() {
        indexMapStack[pointer] = null;
        pathStack[pointer] = null;
        currentPath = null;
        pointer--;
    }

    /**
     * Get the last path element from the stack.
     * 
     * @return the name of the path element
     * @since 1.4.2
     */
    public String peekElement() {
        return peekElement(0);
    }

    /**
     * Get a path element from the stack.
     * 
     * @param i path index
     * @return the name of the path element
     * @since 1.4.2
     * @throws ArrayIndexOutOfBoundsException if the index is &gt;= 0 or &lt;= -depth()
     */
    public String peekElement(final int i) {
        if (i < -pointer || i > 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        final int idx = pointer + i - 1;
        final String name;
        final Integer integer = indexMapStack[idx].get(pathStack[idx]);
        final int index = integer.intValue();
        if (index > 1) {
            final StringBuffer chunk = new StringBuffer(pathStack[idx].length() + 6);
            chunk.append(pathStack[idx]).append('[').append(index).append(']');
            name = chunk.toString();
        } else {
            name = pathStack[idx];
        }
        return name;
    }

    /**
     * Get the depth of the stack.
     * 
     * @return the stack depth
     * @since 1.4.2
     */
    public int depth() {
        return pointer;
    }

    private void resizeStacks(final int newCapacity) {
        final String[] newPathStack = new String[newCapacity];
        @SuppressWarnings("unchecked")
        final Map<String, Integer>[] newIndexMapStack = new Map[newCapacity];
        final int min = Math.min(capacity, newCapacity);
        System.arraycopy(pathStack, 0, newPathStack, 0, min);
        System.arraycopy(indexMapStack, 0, newIndexMapStack, 0, min);
        pathStack = newPathStack;
        indexMapStack = newIndexMapStack;
        capacity = newCapacity;
    }

    /**
     * Current Path in stream.
     */
    public Path getPath() {
        if (currentPath == null) {
            final String[] chunks = new String[pointer + 1];
            chunks[0] = "";
            for (int i = -pointer; ++i <= 0;) {
                final String name = peekElement(i);
                chunks[i + pointer] = name;
            }
            currentPath = new Path(chunks);
        }
        return currentPath;
    }
}
