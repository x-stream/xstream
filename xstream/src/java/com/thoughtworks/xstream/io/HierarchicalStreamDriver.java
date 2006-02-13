package com.thoughtworks.xstream.io;

import java.io.InputStream;
import java.io.OutputStream;
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
    /** @since 1.1.3 */
    HierarchicalStreamReader createReader(InputStream in);

    HierarchicalStreamWriter createWriter(Writer out);
    /** @since 1.1.3 */
    HierarchicalStreamWriter createWriter(OutputStream out);

}
