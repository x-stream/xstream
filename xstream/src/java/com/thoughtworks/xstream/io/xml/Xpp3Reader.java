package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xpp3.Xpp3Dom;

import java.util.LinkedList;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Xpp3Reader
        implements HierarchicalStreamReader {
    private Xpp3Dom current;

    private LinkedList pointers = new LinkedList();

    public Xpp3Reader(Xpp3Dom xpp3Dom) {
        current = xpp3Dom;

        pointers.addLast(new Pointer());
    }

    public String name() {
        return current.getName();
    }

    public String text() {
        String text = null;

        try {
            text = current.getValue();
        } catch (Exception e) {
            // do nothing.
        }

        return text == null ? "" : text;
    }

    public String attribute(String attributeName) {
        String text = null;

        try {
            text = current.getAttribute(attributeName);
        } catch (Exception e) {
            // do nothing.
        }

        return text;
    }

    public boolean nextChild() {
        Pointer pointer = (Pointer) pointers.getLast();

        if (pointer.v < current.getChildCount()) {
            pointers.addLast(new Pointer());

            current = current.getChild(pointer.v);

            pointer.v++;

            return true;
        } else {
            return false;
        }
    }

    public void pop() {
        current = current.getParent();

        pointers.removeLast();
    }

    public Object peek() {
        return current;
    }

    private class Pointer {
        public int v;
    }
}
