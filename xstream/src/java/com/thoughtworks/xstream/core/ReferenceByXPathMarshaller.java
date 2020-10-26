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
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByXPathMarshaller extends AbstractReferenceMarshaller<Path> {

    private final int mode;

    public ReferenceByXPathMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper,
            final int mode) {
        super(writer, converterLookup, mapper);
        this.mode = mode;
    }

    @Override
    protected String createReference(final Path currentPath, final Path existingReferenceKey) {
        final Path existingPath = existingReferenceKey;
        final Path referencePath = (mode & ReferenceByXPathMarshallingStrategy.ABSOLUTE) > 0
            ? existingPath
            : currentPath.relativeTo(existingPath);
        return (mode & ReferenceByXPathMarshallingStrategy.SINGLE_NODE) > 0 ? referencePath.explicit() : referencePath
            .toString();
    }

    @Override
    protected Path createReferenceKey(final Path currentPath, final Object item) {
        return currentPath;
    }

    @Override
    protected void fireValidReference(final Path referenceKey) {
        // nothing to do
    }
}
