package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;

public class PrettyPrintWriter implements HierarchicalStreamWriter {

    private final QuickWriter writer;
    private final LinkedList elementStack = new LinkedList();
    private boolean tagInProgress;
    private int depth;
    private final char[] lineIndenter;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;

    private static final char[] AMP = "&amp;".toCharArray();
    private static final char[] LT = "&lt;".toCharArray();
    private static final char[] GT = "&gt;".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    public PrettyPrintWriter(Writer writer, String lineIndenter) {
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter.toCharArray();
    }

    public PrettyPrintWriter(PrintWriter writer) {
        this(writer, "  ");
    }

    public PrettyPrintWriter(Writer writer) {
        this(new PrintWriter(writer));
    }

    public void startNode(String name) {
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.addLast(name);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();

        // Profiler said this was a bottleneck
        final char[] chars = text.toCharArray();
        final int length = chars.length;
        for (int i = 0; i < length; i++) {
            final char c = chars[i];
            switch (c) {
                case '&':
                    writer.write(AMP);
                    break;
                case '<':
                    writer.write(LT);
                    break;
                case '>':
                    writer.write(GT);
                    break;
                default:
                    writer.write(c);
            }
        }
        // end bottleneck
    }

    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(key);
        writer.write('=');
        writer.write('\"');
        writer.write(value);
        writer.write('\"');
    }

    public void endNode() {
        depth--;
        if (tagIsEmpty) {
            writer.write('/');
            readyForNewLine = false;
            finishTag();
            elementStack.removeLast();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write((String) elementStack.removeLast());
            writer.write('>');
        }
        readyForNewLine = true;
        writer.flush();
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
        writer.write('\n');
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    class QuickWriter {

        private final Writer writer;
        private char[] buffer = new char[32];
        private int pointer;

        public QuickWriter(Writer writer) {
            this.writer = writer;
        }

        private void write(String str) {
            int len = str.length();
            if (pointer + len >= buffer.length) {
                flush();
                if (len > buffer.length) {
                    raw(str.toCharArray());
                    return;
                }
            }
            str.getChars(0, len, buffer, pointer);
            pointer += len;
        }

        private void write(char c) {
            if (pointer + 1 >= buffer.length) {
                flush();
            }
            buffer[pointer++] = c;
        }

        private void write(char[] c) {
            int len = c.length;
            if (pointer + len >= buffer.length) {
                flush();
                if (len > buffer.length) {
                    raw(c);
                    return;
                }
            }
            System.arraycopy(c, 0, buffer, pointer, len);
            pointer += len;
        }

        public void flush() {
            try {
                writer.write(buffer, 0, pointer);
                pointer = 0;
                writer.flush();
            } catch (IOException e) {
                throw new StreamException(e);
            }
        }

        private void raw(char[] c) {
            try {
                writer.write(c);
                writer.flush();
            } catch (IOException e) {
                throw new StreamException(e);
            }
        }
    }

}
