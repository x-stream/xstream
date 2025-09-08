/*
 * Copyright (C) 2004, 2005, 2006, 2023, 2024, 2025 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;

import java.io.Writer;


/**
 * A simple writer that outputs XML in a pretty-printed indented stream.
 * <p>
 * By default, the chars <br>
 * <code>&amp; &lt; &gt; &quot; ' \r</code><br>
 * are escaped and replaced with a suitable XML entity. To alter this behavior, override the
 * {@link #writeText(com.thoughtworks.xstream.io.xml.PrettyPrintWriter.PureWriter, String)} and
 * {@link #writeAttributeValue(com.thoughtworks.xstream.io.xml.PrettyPrintWriter.PureWriter, String)} methods.
 * </p>
 * <p>
 * The XML specification requires XML parsers to drop CR characters completely. This implementation will therefore use
 * only a LF for line endings, never the platform encoding. You can overwrite the {@link #getNewLine()} method for a
 * different behavior.
 * </p>
 * <p>
 * Note: Depending on the XML version some characters cannot be written. Especially a 0 character is never valid in XML,
 * neither directly nor as entity nor within CDATA. However, this writer works by default in a quirks mode, where it
 * will write any character at least as character entity (even a null character). You may switch into XML_1_1 mode
 * (which supports most characters) or XML_1_0 that does only support a very limited number of control characters. See
 * XML specification for version <a href="http://www.w3.org/TR/2006/REC-xml-20060816/#charsets">1.0</a> or
 * <a href="http://www.w3.org/TR/2006/REC-xml11-20060816/#charsets">1.1</a>. If a character is not supported, a
 * {@link StreamException} is thrown. Select a proper parser implementation that respects the version in the XML header
 * (the Xpp3 and MX parsers will also read character entities of normally invalid characters). You may also switch to
 * XML_1_0_REPLACEMENT or XML_1_1_REPLACEMENT mode, which will replace the invalid characters with a U+FFFD replacement
 * character.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class PrettyPrintWriter extends AbstractXmlWriter {

    /** Quirks mode: Writes any character into data stream incl. U+0000. */
    public static int XML_QUIRKS = -1;
    /**
     * XML 1.0 mode: Writes characters according XML 1.0 specification, throws {@link StreamException} for invalid
     * characters.
     */
    public static int XML_1_0 = 0;
    /**
     * XML 1.1 mode: Writes characters according XML 1.1 specification, throws {@link StreamException} for invalid
     * characters.
     */
    public static int XML_1_1 = 1;
    /**
     * XML 1.0 mode: Writes characters according XML 1.0 specification, writes character U+FFFFD as replacement for
     * invalid ones.
     * 
     * @since 1.4.21
     */
    public static int XML_1_0_REPLACEMENT = 2;
    /**
     * XML 1.1 mode: Writes characters according XML 1.1 specification, writes character U+FFFFD as replacement for
     * invalid ones.
     * 
     * @since 1.4.21
     */
    public static int XML_1_1_REPLACEMENT = 3;

    /**
     * Interface to separate internal writer implementation from API.
     *
     * @since upcoming
     */
    public interface PureWriter {
        public void write(String str);

        public void write(char ch);

        public void write(char[] chars);

        public void flush();

        public void close();
    }

    private final QuickWriter writer;
    private final FastStack elementStack = new FastStack(16);
    private final char[] lineIndenter;
    private final int mode;

    private boolean tagInProgress;
    protected int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private String newLine;

    private static final char[] NULL = "&#x0;".toCharArray();
    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] CR = "&#xd;".toCharArray();
    private static final char[] QUOT = "&quot;".toCharArray();
    private static final char[] APOS = "&apos;".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();
    private static final char[] REPLACEMENT = "&#xfffd;".toCharArray();

    private PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder, String newLine) {
        super(nameCoder);
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
        this.mode = mode;
        if (mode < XML_QUIRKS || mode > XML_1_1_REPLACEMENT) {
            throw new IllegalArgumentException("Not a valid XML mode: " + mode);
        }
    }

    /**
     * @since 1.2
     * @deprecated As of 1.3
     */
    public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine, XmlFriendlyReplacer replacer) {
        this(writer, XML_QUIRKS, lineIndenter, replacer, newLine);
    }

    /**
     * @since 1.4
     */
    public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, NameCoder nameCoder) {
        this(writer, mode, lineIndenter, nameCoder, "\n");
    }

    /**
     * @since 1.3
     * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, int, char[], NameCoder)} instead
     */
    public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter, XmlFriendlyReplacer replacer) {
        this(writer, mode, lineIndenter, replacer, "\n");
    }

    /**
     * @deprecated As of 1.3
     */
    public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine) {
        this(writer, lineIndenter, newLine, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.3
     */
    public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter) {
        this(writer, mode, lineIndenter, new XmlFriendlyNameCoder());
    }

    public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this(writer, XML_QUIRKS, lineIndenter);
    }

    /**
     * @deprecated As of 1.3
     */
    public PrettyPrintWriter(Writer writer, String lineIndenter, String newLine) {
        this(writer, lineIndenter.toCharArray(), newLine);
    }

    /**
     * @since 1.3
     */
    public PrettyPrintWriter(Writer writer, int mode, String lineIndenter) {
        this(writer, mode, lineIndenter.toCharArray());
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    /**
     * @since 1.4
     */
    public PrettyPrintWriter(Writer writer, int mode, NameCoder nameCoder) {
        this(writer, mode, new char[]{' ', ' '}, nameCoder);
    }

    /**
     * @since 1.3
     * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, int, NameCoder)} instead
     */
    public PrettyPrintWriter(Writer writer, int mode, XmlFriendlyReplacer replacer) {
        this(writer, mode, new char[]{' ', ' '}, replacer);
    }

    /**
     * @since 1.4
     */
    public PrettyPrintWriter(Writer writer, NameCoder nameCoder) {
        this(writer, XML_QUIRKS, new char[]{' ', ' '}, nameCoder, "\n");
    }

    /**
     * @deprecated As of 1.4 use {@link PrettyPrintWriter#PrettyPrintWriter(Writer, NameCoder)} instead.
     */
    public PrettyPrintWriter(Writer writer, XmlFriendlyReplacer replacer) {
        this(writer, new char[]{' ', ' '}, "\n", replacer);
    }

    /**
     * @since 1.3
     */
    public PrettyPrintWriter(Writer writer, int mode) {
        this(writer, mode, new char[]{' ', ' '});
    }

    public PrettyPrintWriter(Writer writer) {
        this(writer, new char[]{' ', ' '});
    }

    public void startNode(String name) {
        String escapedName = encodeNode(name);
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(escapedName);
        elementStack.push(escapedName);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();

        writeText(writer, text);
    }

    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(encodeAttribute(key));
        writer.write('=');
        writer.write('\"');
        writeAttributeValue(writer, value);
        writer.write('\"');
    }

    /**
     * Write the attribute value into the writer. The implementation will escape all characters, the method can be
     * overloaded to write CDATA sections instead.
     * 
     * @param writer the target writer
     * @param text the attribute's value
     * @since upcoming
     */
    protected void writeAttributeValue(PureWriter writer, String text) {
        writeText(text, true);
    }

    /**
     * Write the value of a text node into the writer. The implementation will escape all characters, the method can be
     * overloaded to write CDATA sections instead.
     * 
     * @param writer the target writer
     * @param text the text node's value
     * @since upcoming
     */
    protected void writeText(PureWriter writer, String text) {
        writeText(text, false);
    }

    /**
     * @deprecated as of upcoming, use {@link #writeAttributeValue(PureWriter, String)} instead
     */
    protected void writeAttributeValue(QuickWriter writer, String text) {
        writeAttributeValue((PureWriter)writer, text);
    }

    /**
     * @deprecated as of upcoming, use {@link #writeText(PureWriter, String)} instead
     */
    protected void writeText(QuickWriter writer, String text) {
        writeText((PureWriter)writer, text);
    }

    private void writeText(String text, boolean isAttribute) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            switch (c) {
            case '\0':
                if (mode == XML_QUIRKS) {
                    this.writer.write(NULL);
                } else if (mode == XML_1_0_REPLACEMENT || mode == XML_1_1_REPLACEMENT) {
                    this.writer.write(REPLACEMENT);
                } else {
                    throw new StreamException("Invalid character 0x0 in XML stream");
                }
                break;
            case '&':
                this.writer.write(AMP);
                break;
            case '<':
                this.writer.write(LT);
                break;
            case '>':
                this.writer.write(GT);
                break;
            case '"':
                this.writer.write(QUOT);
                break;
            case '\'':
                this.writer.write(APOS);
                break;
            case '\r':
                this.writer.write(CR);
                break;
            case '\t':
            case '\n':
                if (!isAttribute) {
                    this.writer.write(c);
                    break;
                }
            default:
                if (Character.isDefined(c) && !Character.isISOControl(c)) {
                    boolean replaced = false;
                    if (mode != XML_QUIRKS) {
                        if (c > '\ud7ff' && c < '\ue000') {
                            if (mode == XML_1_0_REPLACEMENT || mode == XML_1_1_REPLACEMENT) {
                                writer.write(REPLACEMENT);
                                replaced = true;
                            } else {
                                throw new StreamException("Invalid character 0x"
                                    + Integer.toHexString(c)
                                    + " in XML stream");
                            }
                        }
                    }
                    if (!replaced) {
                        this.writer.write(c);
                    }
                } else {
                    boolean replaced = false;
                    if (mode == XML_1_0 || mode == XML_1_0_REPLACEMENT) {
                        if (c < 9
                            || c == '\u000b'
                            || c == '\u000c'
                            || c == '\u000e'
                            || c >= '\u000f' && c <= '\u001f') {
                            if (mode == XML_1_0_REPLACEMENT) {
                                writer.write(REPLACEMENT);
                                replaced = true;
                            } else {
                                throw new StreamException("Invalid character 0x"
                                    + Integer.toHexString(c)
                                    + " in XML 1.0 stream");
                            }
                        }
                    }
                    if (mode != XML_QUIRKS) {
                        if (c == '\ufffe' || c == '\uffff') {
                            if (mode == XML_1_0_REPLACEMENT || mode == XML_1_1_REPLACEMENT) {
                                writer.write(REPLACEMENT);
                                replaced = true;
                            } else {
                                throw new StreamException("Invalid character 0x"
                                    + Integer.toHexString(c)
                                    + " in XML stream");
                            }
                        }
                    }
                    if (!replaced) {
                        this.writer.write("&#x");
                        this.writer.write(Integer.toHexString(c));
                        this.writer.write(';');
                    }
                }
            }
        }
    }

    public void endNode() {
        depth--;
        if (tagIsEmpty) {
            writer.write('/');
            readyForNewLine = false;
            finishTag();
            elementStack.popSilently();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write((String)elementStack.pop());
            writer.write('>');
        }
        readyForNewLine = true;
        if (depth == 0) {
            writer.flush();
        }
    }

    private void finishTag() {
        if (tagInProgress) {
            writer.write('>');
        }
        tagInProgress = false;
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        writer.write(getNewLine());
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    /**
     * Retrieve the line terminator. This method returns always a line feed, since according the XML specification any
     * parser must ignore a carriage return. Overload this method, if you need different behavior.
     * 
     * @return the line terminator
     * @since 1.3
     */
    protected String getNewLine() {
        return newLine;
    }
}
