package com.thoughtworks.xstream.io.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class CompactWriter extends PrettyPrintWriter {

    public CompactWriter(PrintWriter writer) {
        super(writer);
    }

    public CompactWriter(Writer writer) {
        super(writer);
    }

    protected void endOfLine() {
        // override parent: don't write anything at end of line
    }
}
