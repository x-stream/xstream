/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.Writer;


/**
 * A simple writer that outputs JSON in a pretty-printed indented stream. Arrays, Lists and Sets
 * rely on you NOT using XStream.addImplicitCollection(..).
 * 
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public class JsonWriter extends AbstractJsonWriter {

    protected final QuickWriter writer;
    protected final Formatter formatter;
    private int depth;
    private boolean newLineProposed;

    public JsonWriter(Writer writer, char[] lineIndenter, String newLine) {
        this(writer, lineIndenter, newLine, 0);
    }

    public JsonWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    public JsonWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    public JsonWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    public JsonWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }

    /**
     * @since 1.3.1
     */
    public JsonWriter(Writer writer, char[] lineIndenter, String newLine, int mode) {
        super(mode);
        this.writer = new QuickWriter(writer);
        this.formatter = new Formatter(lineIndenter, newLine, true);
        depth = (mode & DROP_ROOT_MODE) == 0 ? -1 : 0;
    }

    /**
     * Create a JsonWriter where the writer mode can be chosen.
     * <p>
     * Following constants can be used as bit mask:
     * <ul>
     * <li>{@link #DROP_ROOT_MODE}: drop the root node</li>
     * <li>{@link #STRICT_MODE}: do not throw {@link ConversionException}, if writer should
     * generate invalid JSON</li>
     * </ul>
     * </p>
     * 
     * @param writer the {@link Writer} where the JSON is written to
     * @param mode the JsonWriter mode
     * @since 1.3.1
     */
    public JsonWriter(Writer writer, int mode) {
        this(writer, new char[]{' ', ' '}, "\n", mode);
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

    /**
     * {@inheritDoc}
     */
    protected void startObject(String name) {
        if (newLineProposed) {
            writeNewLine();
        }
        writer.write('{');
        startNewLine();
        if (name != null) {
            addLabel(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void addLabel(String name) {
        if (newLineProposed) {
            writeNewLine();
        }
        writer.write('"');
        writeText(name);
        writer.write("\":");
        if (formatter.insertSpaceAfterLabel()) {
            writer.write(' ');
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void addValue(String value, Type type) {
        if (newLineProposed) {
            writeNewLine();
        }
        if (type == Type.STRING) {
            writer.write('"');
        }
        writeText(value);
        if (type == Type.STRING) {
            writer.write('"');
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void startArray() {
        if (newLineProposed) {
            writeNewLine();
        }
        writer.write("[");
        startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    protected void nextElement() {
        writer.write(",");
        writeNewLine();
    }

    /**
     * {@inheritDoc}
     */
    protected void endArray() {
        endNewLine();
        writer.write("]");
    }

    /**
     * {@inheritDoc}
     */
    protected void endObject() {
        endNewLine();
        writer.write("}");
    }

    private void startNewLine() {
        if ( ++depth > 0) {
            if (newLineProposed) {
                writeNewLine();
            } else {
                newLineProposed = true;
            }
        }
    }

    private void endNewLine() {
        if (depth-- > 0) {
            if (newLineProposed) {
                newLineProposed = false;
            } else {
                writeNewLine();
            }
        }
    }

    private void writeNewLine() {
        int depth = this.depth;
        writer.write(formatter.getNewLine());
        while (depth-- > 0) {
            writer.write(formatter.getLineIndenter());
        }
        newLineProposed = false;
    }

    private void writeText(String text) {
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
    }

    /**
     * Formatter definition for JSON.
     * 
     * @author J&ouml;rg Schaible
     * @since upcoming
     */
    public static class Formatter {
        private char[] lineIndenter;
        private String newLine;
        private final boolean spaceAfterLabel;

        /**
         * Create a new Formatter.
         * 
         * @param lineIndenter
         * @param newLine
         * @since upcoming
         */
        public Formatter(char[] lineIndenter, String newLine, boolean spaceAfterLabel) {
            this.lineIndenter = lineIndenter;
            this.newLine = newLine;
            this.spaceAfterLabel = spaceAfterLabel;
        }

        /**
         * Retrieve the lineIndenter.
         * 
         * @return the lineIndenter
         * @since upcoming
         */
        public char[] getLineIndenter() {
            return this.lineIndenter;
        }

        /**
         * Retrieve the newLine.
         * 
         * @return the newLine
         * @since upcoming
         */
        public String getNewLine() {
            return this.newLine;
        }

        public boolean insertSpaceAfterLabel() {
            return this.spaceAfterLabel;
        }
    }
}
