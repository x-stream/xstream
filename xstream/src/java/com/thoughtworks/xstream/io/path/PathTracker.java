package com.thoughtworks.xstream.io.path;

import java.util.HashMap;
import java.util.Map;

public class PathTracker {

    private int pointer;
    private int capacity;
    private String[] pathStack;
    private Map[] indexMapStack;

    private String currentPath;

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
        System.out.println(getCurrentPath());
    }

    public void popElement() {
        indexMapStack[pointer] = null;
        currentPath = null;
        pointer--;
        System.out.println(getCurrentPath());
    }

    public String getCurrentPath() {
        if (currentPath == null) {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < pointer; i++) {
                result.append('/');
                result.append(pathStack[i]);
                Integer integer = ((Integer) indexMapStack[i].get(pathStack[i]));
                int index = integer.intValue();
                if (index > 1) {
                    result.append('[').append(index).append(']');
                }
            }
            currentPath = result.toString();
        }
        return currentPath;
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

}
