/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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

public class CompactWriter extends PrettyPrintWriter {

    public CompactWriter(Writer writer) {
        super(writer);
    }

    /**
     * @since 1.3
     */
    public CompactWriter(Writer writer, int mode) {
        super(writer, mode);
    }

    public CompactWriter(Writer writer, XmlFriendlyReplacer replacer) {
        super(writer, replacer);
    }

    /**
     * @since 1.3
     */
    public CompactWriter(Writer writer, int mode, XmlFriendlyReplacer replacer) {
        super(writer, mode, replacer);
    }
    
    protected void endOfLine() {
        // override parent: don't write anything at end of line
    }
}
