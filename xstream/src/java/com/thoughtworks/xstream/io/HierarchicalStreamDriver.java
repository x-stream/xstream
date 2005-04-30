package com.thoughtworks.xstream.io;

import java.io.Reader;
import java.io.Writer;

public interface HierarchicalStreamDriver {

    HierarchicalStreamReader createReader(Reader in);
    HierarchicalStreamWriter createWriter(Writer out);

}
