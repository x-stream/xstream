/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.Writer;

import com.thoughtworks.xstream.io.naming.NameCoder;


public class CompactWriter extends PrettyPrintWriter {

    public CompactWriter(final Writer writer) {
        super(writer);
    }

    /**
     * @since 1.3
     */
    public CompactWriter(final Writer writer, final int mode) {
        super(writer, mode);
    }

    /**
     * @since 1.4
     */
    public CompactWriter(final Writer writer, final NameCoder nameCoder) {
        super(writer, nameCoder);
    }

    /**
     * @since 1.4
     */
    public CompactWriter(final Writer writer, final int mode, final NameCoder nameCoder) {
        super(writer, mode, nameCoder);
    }

    /**
     * @deprecated As of 1.4 use {@link CompactWriter#CompactWriter(Writer, NameCoder)} instead.
     */
    @Deprecated
    public CompactWriter(final Writer writer, final XmlFriendlyReplacer replacer) {
        super(writer, replacer);
    }

    /**
     * @since 1.3
     * @deprecated As of 1.4 use {@link CompactWriter#CompactWriter(Writer, int, NameCoder)} instead.
     */
    @Deprecated
    public CompactWriter(final Writer writer, final int mode, final XmlFriendlyReplacer replacer) {
        super(writer, mode, replacer);
    }

    @Override
    protected void endOfLine() {
        // override parent: don't write anything at end of line
    }
}
