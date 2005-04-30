package com.thoughtworks.xstream.io;

import java.io.Reader;
import java.io.Writer;

/**
 * Provides implementation of XML parsers and writers to XStream.
 *
 * @author Joe Walnes
 * @author James Strachan
 */
public interface HierarchicalStreamDriver {

    HierarchicalStreamReader createReader(Reader in);

    HierarchicalStreamWriter createWriter(Writer out);

}
