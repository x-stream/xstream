package com.thoughtworks.xstream.io;

import java.io.Reader;

public interface HierarchicalStreamDriver {

    HierarchicalStreamReader createReader(Reader text);

}
