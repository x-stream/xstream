package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

import java.util.LinkedList;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XppDomReader
        implements HierarchicalStreamReader {
    private Xpp3Dom current;

    private LinkedList pointers = new LinkedList();

    public XppDomReader(Xpp3Dom xpp3Dom) {
        current = xpp3Dom;

        pointers.addLast(new Pointer());
    }

    public String getNodeName() {
        return current.getName();
    }

    public String getValue() {
        String text = null;

        try {
            text = current.getValue();
        } catch (Exception e) {
            // do nothing.
        }

        return text == null ? "" : text;
    }

    public String getAttribute(String attributeName) {
        String text = null;

        try {
            text = current.getAttribute(attributeName);
        } catch (Exception e) {
            // do nothing.
        }

        return text;
    }

    public Object peekUnderlyingNode() {
        return current;
    }

    private class Pointer {
        public int v;
    }

    public boolean hasMoreChildren() {
        Pointer pointer = (Pointer) pointers.getLast();

        if (pointer.v < current.getChildCount()) {
            return true;
        } else {
            return false;
        }
    }

    public void moveUp() {
        current = current.getParent();

        pointers.removeLast();
    }

    public void moveDown() {
        Pointer pointer = (Pointer) pointers.getLast();
        pointers.addLast(new Pointer());

        current = current.getChild(pointer.v);

        pointer.v++;

    }

    public void appendErrors(ErrorWriter errorWriter) {
    }
}
