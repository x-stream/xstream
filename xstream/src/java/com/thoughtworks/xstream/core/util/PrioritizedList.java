package com.thoughtworks.xstream.core.util;

import java.util.Iterator;

/**
 * List that allows items to be added with a priority that will affect the order in which they are later iterated over.
 *
 * Objects with a high priority will appear before objects with a low priority in the list. If two objects of the same
 * priority are added to the list, the most recently added one will be iterated over first.
 *
 * @author Joe Walnes
 */
public class PrioritizedList {

    /**
     * Start of forward only linked list. Each item contains a value, priority and pointer to next item.
     */
    private LinkedItem first;

    /**
     * Add an item with a default priority of zero.
     */
    public void add(Object item) {
        add(item, 0);
    }

    public void add(Object item, int priority) {
        if (first == null || priority >= first.priority) {
            first = new LinkedItem(item, priority, first);
        } else {
            // Note: this is quite efficient if the client tends to add low priority items before high priority items
            // as it will not have to iterate over much of the list. However for the other way round, maybe some
            // optimizations can be made? -joe
            for (LinkedItem current = first; current != null; current = current.next) {
                if (current.next == null || priority >= current.next.priority) {
                    current.next = new LinkedItem(item, priority, current.next);
                    return;
                }
            }
        }
    }

    public Iterator iterator() {
        return new LinkedItemIterator(first);
    }

    private static class LinkedItem {

        final Object value;
        final int priority;

        LinkedItem next;

        public LinkedItem(Object value, int priority, LinkedItem next) {
            this.value = value;
            this.priority = priority;
            this.next = next;
        }

    }

    private static class LinkedItemIterator implements Iterator {

        private LinkedItem current;

        public LinkedItemIterator(LinkedItem current) {
            this.current = current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return current != null;
        }

        public Object next() {
            Object result = current.value;
            current = current.next;
            return result;
        }

    }

}
