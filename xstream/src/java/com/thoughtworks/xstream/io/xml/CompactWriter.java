/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
