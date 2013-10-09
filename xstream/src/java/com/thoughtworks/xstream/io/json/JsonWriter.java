/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.io.Writer;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


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
    protected final Format format;
    private int depth;
    private boolean newLineProposed;

    /**
     * @deprecated As of 1.4 use {@link JsonWriter#JsonWriter(Writer, Format) instead}
     */
    public JsonWriter(Writer writer, char[] lineIndenter, String newLine) {
        this(writer, 0, new Format(
            lineIndenter, newLine.toCharArray(), Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT));
    }

    /**
     * @deprecated As of 1.4 use {@link JsonWriter#JsonWriter(Writer, Format) instead}
     */
    public JsonWriter(Writer writer, char[] lineIndenter) {
        this(writer, 0, new Format(lineIndenter, new char[]{'\n'}, Format.SPACE_AFTER_LABEL
            | Format.COMPACT_EMPTY_ELEMENT));
    }

    /**
     * @deprecated As of 1.4 use {@link JsonWriter#JsonWriter(Writer, Format) instead}
     */
    public JsonWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, 0, new Format(
            lineIndenter.toCharArray(), newLine.toCharArray(), Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT));
    }

    /**
     * @deprecated As of 1.4 use {@link JsonWriter#JsonWriter(Writer, Format) instead}
     */
    public JsonWriter(Writer writer, String lineIndenter) {
        this(writer, 0, new Format(
            lineIndenter.toCharArray(), new char[]{'\n'}, Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT));
    }

    public JsonWriter(Writer writer) {
        this(writer, 0, new Format(
            new char[]{' ', ' '}, new char[]{'\n'}, Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT));
    }

    /**
     * @since 1.3.1
     * @deprecated As of 1.4 use {@link JsonWriter#JsonWriter(Writer, int, Format) instead}
     */
    public JsonWriter(Writer writer, char[] lineIndenter, String newLine, int mode) {
        this(writer, mode, new Format(
            lineIndenter, newLine.toCharArray(), Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT));
    }

    /**
     * Create a JsonWriter where the writer mode can be chosen.
     * 
     * @param writer the {@link Writer} where the JSON is written to
     * @param mode the JsonWriter mode
     * @since 1.3.1
     * @see #JsonWriter(Writer, int, Format)
     */
    public JsonWriter(Writer writer, int mode) {
        this(writer, mode, new Format());
    }

    /**
     * Create a JsonWriter where the format is provided.
     * 
     * @param writer the {@link Writer} where the JSON is written to
     * @param format the JSON format definition
     * @since 1.4
     * @see #JsonWriter(Writer, int, Format)
     */
    public JsonWriter(Writer writer, Format format) {
        this(writer, 0, format);
    }

    /**
     * Create a JsonWriter where the writer mode can be chosen and the format definition is
     * provided.
     * <p>
     * Following constants can be used as bit mask for the mode:
     * </p>
     * <ul>
     * <li>{@link #DROP_ROOT_MODE}: drop the root node</li>
     * <li>{@link #STRICT_MODE}: do not throw {@link ConversionException}, if writer should
     * generate invalid JSON</li>
     * <li>{@link #EXPLICIT_MODE}: ensure that all available data is explicitly written even if
     * addition objects must be added</li>
     * </ul>
     * 
     * @param writer the {@link Writer} where the JSON is written to
     * @param mode the JsonWriter mode
     * @param format the JSON format definition
     * @since 1.4
     */
    public JsonWriter(Writer writer, int mode, Format format) {
        this(writer, mode, format, 1024);
    }

    /**
     * Create a JsonWriter.
     * 
     * @param writer the {@link Writer} where the JSON is written to
     * @param mode the JsonWriter mode
     * @param format the JSON format definition
     * @param bufferSize the buffer size of the internally used QuickWriter
     * @see JsonWriter#JsonWriter(Writer, int, Format)
     * @since 1.4
     */
    public JsonWriter(Writer writer, int mode, Format format, int bufferSize) {
        super(mode, format.getNameCoder());
        this.writer = new QuickWriter(writer, bufferSize);
        this.format = format;
        depth = (mode & DROP_ROOT_MODE) == 0 ? -1 : 0;
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
    protected void startObject() {
        if (newLineProposed) {
            writeNewLine();
        }
        writer.write('{');
        startNewLine();
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
        if ((format.mode() & Format.SPACE_AFTER_LABEL) != 0) {
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
            newLineProposed = true;
        }
    }

    private void endNewLine() {
        if (depth-- > 0) {
            if (((format.mode() & Format.COMPACT_EMPTY_ELEMENT) != 0) && newLineProposed) {
                newLineProposed = false;
            } else {
                writeNewLine();
            }
        }
    }

    private void writeNewLine() {
        int depth = this.depth;
        writer.write(format.getNewLine());
        while (depth-- > 0) {
            writer.write(format.getLineIndenter());
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
            // turn this off - it is no CTRL char anyway
            // case '/':
            // this.writer.write("\\/");
            // break;
            case '\b':
                this.writer.write("\\b");
                break;
            case '\f':
                this.writer.write("\\f");
                break;
            case '\n':
                this.writer.write("\\n");
                break;
            case '\r':
                this.writer.write("\\r");
                break;
            case '\t':
                this.writer.write("\\t");
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
     * Format definition for JSON.
     * 
     * @author J&ouml;rg Schaible
     * @since 1.4
     */
    public static class Format {

        public static int SPACE_AFTER_LABEL = 1;
        public static int COMPACT_EMPTY_ELEMENT = 2;

        private char[] lineIndenter;
        private char[] newLine;
        private final int mode;
        private final NameCoder nameCoder;

        /**
         * Create a new default Formatter. The formatter uses two spaces, normal line feed
         * character, adds a space after the label and will try to compact the output.
         * 
         * @since 1.4.2
         */
        public Format() {
            this(new char[]{' ', ' '}, new char[]{'\n'}, Format.SPACE_AFTER_LABEL
                | Format.COMPACT_EMPTY_ELEMENT);
        }


        /**
         * Create a new Formatter.
         * 
         * @param lineIndenter the characters used for indenting the line
         * @param newLine the characters used to create a new line
         * @param mode the flags for the format modes
         * @since 1.4
         */
        public Format(char[] lineIndenter, char[] newLine, int mode) {
            this(lineIndenter, newLine, mode, new NoNameCoder());
        }

        /**
         * Create a new Formatter.
         * 
         * @param lineIndenter the characters used for indenting the line
         * @param newLine the characters used to create a new line
         * @param mode the flags for the format modes
         * @param nameCoder the name encoder and decoder
         * @since 1.4.2
         */
        public Format(char[] lineIndenter, char[] newLine, int mode, NameCoder nameCoder) {
            this.lineIndenter = lineIndenter;
            this.newLine = newLine;
            this.mode = mode;
            this.nameCoder = nameCoder;
        }

        /**
         * Retrieve the lineIndenter.
         * 
         * @return the lineIndenter
         * @since 1.4
         */
        public char[] getLineIndenter() {
            return this.lineIndenter;
        }

        /**
         * Retrieve the newLine.
         * 
         * @return the newLine
         * @since 1.4
         */
        public char[] getNewLine() {
            return this.newLine;
        }

        /**
         * Retrieve the mode flags of the formatter.
         * 
         * @return the mode
         * @since 1.4
         */
        public int mode() {
            return this.mode;
        }


        /**
         * Retrieve the NameCoder.
         * 
         * @return the name coder
         * @since 1.4.2
         */
        public NameCoder getNameCoder() {
            return nameCoder;
        }
    }
}
