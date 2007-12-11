/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
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
