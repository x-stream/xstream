package com.thoughtworks.xstream.io.xml;

import java.io.Writer;

public class CompactWriter extends PrettyPrintWriter {

    public CompactWriter(Writer writer) {
        super(writer);
    }

    protected void endOfLine() {
        // override parent: don't write anything at end of line
    }
}
