package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;

import java.io.Writer;
import java.util.Collection;


/**
 * A simple writer that outputs JSON in a pretty-printed indented stream. Arrays, Lists and Sets
 * rely on you NOT using XStream.addImplicitCollection(..)
 * 
 * @author Paul Hammant
 * @since 1.2
 */
public class JsonHierarchicalStreamWriter implements ExtendedHierarchicalStreamWriter {

    private final QuickWriter writer;
    private final FastStack elementStack = new FastStack(16);
    private final char[] lineIndenter;

    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private String newLine;

    public JsonHierarchicalStreamWriter(Writer writer, char[] lineIndenter, String newLine) {
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
    }

    public JsonHierarchicalStreamWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    public JsonHierarchicalStreamWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    public JsonHierarchicalStreamWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    public JsonHierarchicalStreamWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }

    /**
     * @deprecated Use startNode(String name, Class clazz) instead.
     */

    public void startNode(String name) {
        startNode(name, null);
    }

    public void startNode(String name, Class clazz) {
        Node currNode = (Node)elementStack.peek();
        if (currNode == null) {
            writer.write("{");
        }
        if (currNode != null && currNode.fieldAlready) {
            writer.write(",");
        }
        tagIsEmpty = false;
        finishTag();
        if (currNode == null || (currNode.clazz != null && !currNode.isCollection)) {
            writer.write("\"");
            writer.write(name);
            writer.write("\": ");
        }
        if (clazz != null && (Collection.class.isAssignableFrom(clazz) || clazz.isArray())) {
            writer.write("[");
        } else if (hasChildren(clazz)) {
            writer.write("{");
        }
        if (currNode != null) {
            currNode.fieldAlready = true;
        }
        elementStack.push(new Node(name, clazz));
        depth++ ;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public class Node {
        public final String name;
        public final Class clazz;
        public boolean fieldAlready;
        public boolean isCollection;

        public Node(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
            isCollection = clazz != null
                && (Collection.class.isAssignableFrom(clazz) || clazz.isArray());
        }
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();
        writeText(writer, text);
    }

    public void addAttribute(String key, String value) {
        Node currNode = (Node)elementStack.peek();
        if (currNode == null || !currNode.isCollection) {
            startNode('@' + key, String.class);
            writeText(value, String.class);
            endNode();
        }
    }

    protected void writeAttributeValue(QuickWriter writer, String text) {
        writeText(text, null);
    }

    protected void writeText(QuickWriter writer, String text) {
        Node foo = (Node)elementStack.peek();

        writeText(text, foo.clazz);
    }

    private void writeText(String text, Class clazz) {
        if (needsQuotes(clazz)) {
            writer.write("\"");
        }
        if ((clazz == Character.class || clazz == Character.TYPE) && "".equals(text)) {
            text = "\0";
        }

        int i = 0;
        while (true) {
            int idxQuote = text.indexOf('"', i);
            int idxSlash = text.indexOf('\\', i);
            int idxNull = text.indexOf('\0', i);
            int idx = Math.min(Math.min(
                idxQuote < 0 ? Integer.MAX_VALUE : idxQuote, idxSlash < 0
                    ? Integer.MAX_VALUE
                    : idxSlash), idxNull < 0 ? Integer.MAX_VALUE : idxNull);
            if (idx == Integer.MAX_VALUE) {
                break;
            }
            if (idx != 0) {
                this.writer.write(text.substring(i, idx));
            }
            if (idx == idxQuote) {
                this.writer.write("\\\"");
            } else if (idx == idxSlash) {
                this.writer.write("\\\\");
            } else {
                this.writer.write("\\u00");
            }
            i = idx + 1;
        }

        this.writer.write(text.substring(i));
        if (needsQuotes(clazz)) {
            writer.write("\"");
        }
    }

    private boolean needsQuotes(Class clazz) {
        clazz = clazz != null && clazz.isPrimitive() ? clazz : Primitives.unbox(clazz);
        return clazz == null || clazz == Character.TYPE;
    }

    public void endNode() {
        depth-- ;
        Node node = (Node)elementStack.pop();
        if (tagIsEmpty && !hasChildren(node.clazz)) {
            readyForNewLine = false;
            finishTag();
        } else {
            finishTag();
            if (node.clazz != null
                && (Collection.class.isAssignableFrom(node.clazz) || node.clazz.isArray())) {
                writer.write("]");
            } else if (hasChildren(node.clazz)) {
                writer.write("}");
            }
        }
        readyForNewLine = true;
        if (depth == 0) {
            writer.write("}");
            writer.flush();
        }
    }

    private boolean hasChildren(Class clazz) {
        if (clazz == String.class || clazz == Character.TYPE || clazz == Character.class) {
            return false;
        } else {
            return needsQuotes(clazz);
        }
    }

    private void finishTag() {
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        writer.write(newLine);
        for (int i = 0; i < depth; i++ ) {
            writer.write(lineIndenter);
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }
}
