package com.thoughtworks.xstream.io.path;

import java.util.HashMap;
import java.util.Map;

public class PathTracker {

    private int pointer;
    private int capacity;
    private String[] pathStack;
    private Map[] indexMapStack;

    private Path currentPath;

    public PathTracker() {
        this(16);
    }

    public PathTracker(int initialCapacity) {
        this.capacity = initialCapacity;
        pathStack = new String[capacity];
        indexMapStack = new Map[capacity];
    }

    public void pushElement(String name) {
        if (pointer + 1 >= capacity) {
            resizeStacks(capacity * 2);
        }
        pathStack[pointer] = name;
        Map indexMap = indexMapStack[pointer];
        if (indexMap == null) {
            indexMap = new HashMap();
            indexMapStack[pointer] = indexMap;
        }
        if (indexMap.containsKey(name)) {
            indexMap.put(name, new Integer(((Integer) indexMap.get(name)).intValue() + 1));
        } else {
            indexMap.put(name, new Integer(1));
        }
        pointer++;
        currentPath = null;
    }

    public void popElement() {
        indexMapStack[pointer] = null;
        currentPath = null;
        pointer--;
    }

    /**
     * @deprecated Use {@link #getPath()} instead.
     */
    public String getCurrentPath() {
        return getPath().toString();
    }

    private void resizeStacks(int newCapacity) {
        String[] newPathStack = new String[newCapacity];
        Map[] newIndexMapStack = new Map[newCapacity];
        int min = Math.min(capacity, newCapacity);
        System.arraycopy(pathStack, 0, newPathStack, 0, min);
        System.arraycopy(indexMapStack, 0, newIndexMapStack, 0, min);
        pathStack = newPathStack;
        indexMapStack = newIndexMapStack;
        capacity = newCapacity;
    }

    public Path getPath() {
        if (currentPath == null) {
            String[] chunks = new String[pointer];
            for (int i = 0; i < pointer; i++) {
                Integer integer = ((Integer) indexMapStack[i].get(pathStack[i]));
                int index = integer.intValue();
                if (index > 1) {
                    chunks[i] = pathStack[i] + '[' + index + ']';
                } else {
                    chunks[i] = pathStack[i];
                }
            }
            currentPath = new Path(chunks);
        }
        return currentPath;
    }
}
