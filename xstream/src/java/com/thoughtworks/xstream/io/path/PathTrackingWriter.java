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

package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;


/**
 * Wrapper for HierarchicalStreamWriter that tracks the path (a subset of XPath) of the current node that is being
 * written.
 * 
 * @see PathTracker
 * @see Path
 * @author Joe Walnes
 */
public class PathTrackingWriter extends WriterWrapper {

    private final PathTracker pathTracker;
    private final boolean isNameEncoding;

    public PathTrackingWriter(final HierarchicalStreamWriter writer, final PathTracker pathTracker) {
        super(writer);
        isNameEncoding = writer.underlyingWriter() instanceof AbstractWriter;
        this.pathTracker = pathTracker;
    }

    @Override
    public void startNode(final String name) {
        pathTracker.pushElement(isNameEncoding ? ((AbstractWriter)wrapped.underlyingWriter()).encodeNode(name) : name);
        super.startNode(name);
    }

    @Override
    public void startNode(final String name, final Class<?> clazz) {
        pathTracker.pushElement(isNameEncoding ? ((AbstractWriter)wrapped.underlyingWriter()).encodeNode(name) : name);
        super.startNode(name, clazz);
    }

    @Override
    public void endNode() {
        super.endNode();
        pathTracker.popElement();
    }
}
