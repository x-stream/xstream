/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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

import java.io.Writer;


/**
 * A simple writer that outputs XML in a pretty-printed indented stream.
 * <p>
 * By default, the chars <code><pre>
 * &amp; &lt; &gt; &quot; ' \r
 * </pre></code> are escaped and replaced with a suitable XML entity. To alter this behavior, override
 * the the {@link #writeText(com.thoughtworks.xstream.core.util.QuickWriter, String)} and
 * {@link #writeAttributeValue(com.thoughtworks.xstream.core.util.QuickWriter, String)} methods.
 * </p>
 * <p>
 * Note: Depending on the XML version some characters cannot be written. Especially a 0
 * character is never valid in XML, neither directly nor as entity nor within CDATA. However, this writer
 * works by default in a quirks mode, where it will write any character at least as character entity (even
 * a null character). You may switch into XML_1_1 mode (which supports most characters) or XML_1_0
 * that does only support a very limited number of control characters. See XML specification for version
 * <a href="http://www.w3.org/TR/2006/REC-xml-20060816/#charsets">1.0</a> or
 * <a href="http://www.w3.org/TR/2006/REC-xml11-20060816/#charsets">1.1</a>. If a character is 
 * not supported, a {@link StreamException} is thrown. Select a proper parser implementation that
 * respects the version in the XML header (the Xpp3 parser will also read character entities of normally
 * invalid characters).
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class PrettyPrintWriter extends AbstractXmlWriter {

    public static int XML_QUIRKS = -1;
    public static int XML_1_0 = 0;
    public static int XML_1_1 = 1;

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

    private PrettyPrintWriter(
        Writer writer, int mode, char[] lineIndenter, XmlFriendlyReplacer replacer,
        String newLine) {
        super(replacer);
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
        this.newLine = newLine;
        this.mode = mode;
        if (mode < XML_QUIRKS || mode > XML_1_1) {
            throw new IllegalArgumentException("Not a valid XML mode");
        }
    }

    /**
     * @since 1.2
     * @deprecated since 1.3
     */
    public PrettyPrintWriter(
        Writer writer, char[] lineIndenter, String newLine, XmlFriendlyReplacer replacer) {
        this(writer, XML_QUIRKS, lineIndenter, replacer, newLine);
    }

    /**
     * @since 1.3
     */
    public PrettyPrintWriter(
        Writer writer, int mode, char[] lineIndenter, XmlFriendlyReplacer replacer) {
        this(writer, mode, lineIndenter, replacer, "\n");
    }

    /**
     * @deprecated since 1.3
     */
    public PrettyPrintWriter(Writer writer, char[] lineIndenter, String newLine) {
        this(writer, lineIndenter, newLine, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.3
     */
    public PrettyPrintWriter(Writer writer, int mode, char[] lineIndenter) {
        this(writer, mode, lineIndenter, new XmlFriendlyReplacer());
    }

    public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this(writer, lineIndenter, "\n");
    }

    /**
     * @deprecated since 1.3
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
     * @since 1.3
     */
    public PrettyPrintWriter(Writer writer, int mode, XmlFriendlyReplacer replacer) {
        this(writer, mode, new char[]{' ', ' '}, replacer);
    }

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
        String escapedName = escapeXmlName(name);
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(escapedName);
        elementStack.push(escapedName);
        tagInProgress = true;
        depth++ ;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();

        writeText(writer, text);
    }

    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(escapeXmlName(key));
        writer.write('=');
        writer.write('\"');
        writeAttributeValue(writer, value);
        writer.write('\"');
    }

    protected void writeAttributeValue(QuickWriter writer, String text) {
        writeText(text);
    }

    protected void writeText(QuickWriter writer, String text) {
        writeText(text);
    }

    private void writeText(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++ ) {
            char c = text.charAt(i);
            switch (c) {
            case '\0':
                if (mode == XML_QUIRKS) {
                    this.writer.write(NULL);
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
                this.writer.write(c);
                break;
            default:
                if (Character.isDefined(c) && !Character.isISOControl(c)) {
                    if (mode != XML_QUIRKS) {
                        if (c > '\ud7ff' && c < '\ue000') {
                            throw new StreamException("Invalid character 0x"
                                + Integer.toHexString(c)
                                + " in XML stream");
                        }
                    }
                    this.writer.write(c);
                } else {
                    if (mode == XML_1_0) {
                        if (c < 9
                            || c == '\u000b'
                            || c == '\u000c'
                            || c == '\u000e'
                            || c == '\u000f') {
                            throw new StreamException("Invalid character 0x"
                                + Integer.toHexString(c)
                                + " in XML 1.0 stream");
                        }
                    }
                    if (mode != XML_QUIRKS) {
                        if (c == '\ufffe' || c == '\uffff') {
                            throw new StreamException("Invalid character 0x"
                                + Integer.toHexString(c)
                                + " in XML stream");
                        }
                    }
                    this.writer.write("&#x");
                    this.writer.write(Integer.toHexString(c));
                    this.writer.write(';');
                }
            }
        }
    }

    public void endNode() {
        depth-- ;
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

    protected String getNewLine() {
        return newLine;
    }
}
