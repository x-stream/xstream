package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.Converter;

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
     * The first item does not contain a value, rather just a pointer to the next real item. This makes
     * the add() algorithm easier as there is no special case for adding to the beginning of the list.
     */
    private final LinkedItem pointerToFirst = new LinkedItem(null, 0, null);

    private int lowestPriority = Integer.MAX_VALUE;

    /**
     * Add an item with a default priority of zero.
     */
    public void add(Object item) {
        add(item, 0);
    }

    public void add(Object item, int priority) {
        // Note: this is quite efficient if the client tends to add low priority items before high priority items
        // as it will not have to iterate over much of the list. However for the other way round, maybe some
        // optimizations can be made? -joe
        LinkedItem current = pointerToFirst;
        while(current.next != null && priority < current.next.priority) {
            current = current.next;
        }
        current.next = new LinkedItem(item, priority, current.next);
        if (priority < lowestPriority) {
            lowestPriority = priority;
        }
    }

    public Iterator iterator() {
        return new LinkedItemIterator(pointerToFirst.next);
    }

    public Object firstOfLowestPriority() {
        for(LinkedItem current = pointerToFirst.next; current != null; current = current.next) {
            if (current.priority == lowestPriority) {
                return current.value;
            }
        }
        return null;
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
