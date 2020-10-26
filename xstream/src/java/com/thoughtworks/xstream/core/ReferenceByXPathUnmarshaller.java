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

package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.AbstractReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByXPathUnmarshaller extends AbstractReferenceUnmarshaller<Path> {

    private final PathTracker pathTracker = new PathTracker();
    protected boolean isNameEncoding;

    public ReferenceByXPathUnmarshaller(
            final Object root, final HierarchicalStreamReader reader, final ConverterLookup converterLookup,
            final Mapper mapper) {
        super(root, reader, converterLookup, mapper);
        this.reader = new PathTrackingReader(reader, pathTracker);
        isNameEncoding = reader.underlyingReader() instanceof AbstractReader;
    }

    @Override
    protected Path getReferenceKey(final String reference) {
        final Path path = new Path(isNameEncoding
            ? ((AbstractReader)reader.underlyingReader()).decodeNode(reference)
            : reference);
        // We have absolute references, if path starts with '/'
        return reference.charAt(0) != '/' ? pathTracker.getPath().apply(path) : path;
    }

    @Override
    protected Path getCurrentReferenceKey() {
        return pathTracker.getPath();
    }

}
