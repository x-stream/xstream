package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public abstract class AbstractTreeReader implements HierarchicalStreamReader {

    private FastStack pointers = new FastStack(16);
    private Object current;

    protected AbstractTreeReader(Object rootElement) {
        this.current = rootElement;
        pointers.push(new Pointer());
        reassignCurrentElement(current);
    }

    protected abstract void reassignCurrentElement(Object current);
    protected abstract Object getParent();
    protected abstract Object getChild(int index);
    protected abstract int getChildCount();

    private static class Pointer {
        public int v;
    }

    public boolean hasMoreChildren() {
        Pointer pointer = (Pointer) pointers.peek();

        if (pointer.v < getChildCount()) {
            return true;
        } else {
            return false;
        }
    }

    public void moveUp() {
        current = getParent();
        pointers.popSilently();
        reassignCurrentElement(current);
    }

    public void moveDown() {
        Pointer pointer = (Pointer) pointers.peek();
        pointers.push(new Pointer());

        current = getChild(pointer.v);

        pointer.v++;
        reassignCurrentElement(current);
    }

    public void appendErrors(ErrorWriter errorWriter) {
    }

    public Object peekUnderlyingNode() {
        return current;
    }
}
