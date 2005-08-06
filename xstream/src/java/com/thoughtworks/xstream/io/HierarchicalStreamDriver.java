package com.thoughtworks.xstream.io;

import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides implementation of XML parsers and writers to XStream.
 *
 * @author Joe Walnes
 * @author James Strachan
 */
public interface HierarchicalStreamDriver {

    HierarchicalStreamReader createReader(Reader in);
    /** @since 1.2 */
    HierarchicalStreamReader createReader(InputStream in);

    HierarchicalStreamWriter createWriter(Writer out);
    /** @since 1.2 */
    HierarchicalStreamWriter createWriter(OutputStream out);

}
