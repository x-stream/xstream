package com.thoughtworks.xstream.xml.text;

import java.io.PrintWriter;
import java.io.Writer;

public class CompactXMLWriter extends PrettyPrintXMLWriter {

    public CompactXMLWriter(PrintWriter writer) {
        super(writer);
    }

    public CompactXMLWriter(Writer writer) {
        super(writer);
    }

    protected void endOfLine() {
        // override parent: don't write anything at end of line
    }
}
