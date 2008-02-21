/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;

import java.io.Writer;
import java.util.Collection;
import java.util.Map;


/**
 * A simple writer that outputs JSON in a pretty-printed indented stream. Arrays, Lists and Sets
 * rely on you NOT using XStream.addImplicitCollection(..)
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
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
            readyForNewLine = true;
        }
        tagIsEmpty = false;
        finishTag();
        if (currNode == null
            || currNode.clazz == null
            || (currNode.clazz != null && !currNode.isCollection)) {
            if (currNode != null && !currNode.fieldAlready) {
                writer.write("{");
                readyForNewLine = true;
                finishTag();
            }
            writer.write("\"");
            writer.write(name);
            writer.write("\": ");
        }
        if (isCollection(clazz)) {
            writer.write("[");
            readyForNewLine = true;
        }
        if (currNode != null) {
            currNode.fieldAlready = true;
        }
        elementStack.push(new Node(name, clazz));
        depth++ ;
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
            isCollection = isCollection(clazz);
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
            tagIsEmpty = false;
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

        int length = text.length();
        for (int i = 0; i < length; i++ ) {
            char c = text.charAt(i);
            switch (c) {
            case '"':
                this.writer.write("\\\"");
                break;
            case '\\':
                this.writer.write("\\\\");
                break;
            default:
                if (c > 0x1f) {
                    this.writer.write(c);
                } else {
                    this.writer.write("\\u");
                    String hex = "000" + Integer.toHexString(c);
                    this.writer.write(hex.substring(hex.length() - 4));
                }
            }
        }

        if (needsQuotes(clazz)) {
            writer.write("\"");
        }
    }

    private boolean isCollection(Class clazz) {
        return clazz != null
            && (Collection.class.isAssignableFrom(clazz)
                || clazz.isArray()
                || Map.class.isAssignableFrom(clazz) || Map.Entry.class.isAssignableFrom(clazz));
    }

    private boolean needsQuotes(Class clazz) {
        clazz = clazz != null && clazz.isPrimitive() ? clazz : Primitives.unbox(clazz);
        return clazz == null || clazz == Character.TYPE;
    }

    public void endNode() {
        depth-- ;
        Node node = (Node)elementStack.pop();
        if (node.clazz != null && node.isCollection) {
            if (node.fieldAlready) {
                readyForNewLine = true;
            }
            finishTag();
            writer.write("]");
        } else if (tagIsEmpty) {
            readyForNewLine = false;
            writer.write("{}");
            finishTag();
        } else {
            finishTag();
            if (node.fieldAlready) {
                writer.write("}");
            }
        }
        readyForNewLine = true;
        if (depth == 0) {
            writer.write("}");
            writer.flush();
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
