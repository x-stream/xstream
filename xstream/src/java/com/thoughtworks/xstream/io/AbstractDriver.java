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

package com.thoughtworks.xstream.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;


/**
 * Abstract base class for all HierarchicalStreamDriver implementations. Implementations of
 * {@link HierarchicalStreamDriver} should rather be derived from this class then implementing the interface directly.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractDriver implements HierarchicalStreamDriver {

    private final NameCoder replacer;

    /**
     * Creates an AbstractDriver with a NameCoder that does nothing.
     */
    public AbstractDriver() {
        this(new NoNameCoder());
    }

    /**
     * Creates an AbstractDriver with a provided {@link NameCoder}.
     * 
     * @param nameCoder the name coder for the target format
     */
    public AbstractDriver(final NameCoder nameCoder) {
        replacer = nameCoder;
    }

    protected NameCoder getNameCoder() {
        return replacer;
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        try {
            return createReader(in.openStream());
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public HierarchicalStreamReader createReader(final File in) {
        try {
            return createReader(new FileInputStream(in));
        } catch (final FileNotFoundException e) {
            throw new StreamException(e);
        }
    }
}
