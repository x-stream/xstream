package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.core.util.StringStack;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.PrintWriter;
import java.io.Writer;

public class PrettyPrintWriter implements HierarchicalStreamWriter {

    private final QuickWriter writer;
    private final StringStack elementStack = new StringStack(16);
    private final char[] lineIndenter;

    private boolean tagInProgress;
    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;

    private static final char[] AMP = {'&', 'a', 'm', 'p', ';'};
    private static final char[] LT = {'&', 'l', 't', ';'};
    private static final char[] GT = {'&', 'g', 't', ';'};
    private static final char[] CLOSE = {'<', '/'};

    public PrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
    }

    public PrettyPrintWriter(Writer writer, String lineIndenter) {
        this(writer, lineIndenter.toCharArray());
    }

    public PrettyPrintWriter(PrintWriter writer) {
        this(writer, new char[]{' ', ' '});
    }

    public PrettyPrintWriter(Writer writer) {
        this(new PrintWriter(writer));
    }

    public void startNode(String name) {
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.push(name);
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
            elementStack.popSilently();
        } else {
            finishTag();
            writer.write(CLOSE);
            writer.write(elementStack.pop());
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

}
