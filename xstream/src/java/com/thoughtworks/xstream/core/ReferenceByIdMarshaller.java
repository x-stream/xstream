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


public class ReferenceByIdMarshaller extends AbstractReferenceMarshaller<String> {

    private final IDGenerator idGenerator;

    public static interface IDGenerator {
        String next(Object item);
    }

    public ReferenceByIdMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper,
            final IDGenerator idGenerator) {
        super(writer, converterLookup, mapper);
        this.idGenerator = idGenerator;
    }

    public ReferenceByIdMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper) {
        this(writer, converterLookup, mapper, new SequenceGenerator(1));
    }

    @Override
    protected String createReference(final Path currentPath, final String existingReferenceKey) {
        return existingReferenceKey.toString();
    }

    @Override
    protected String createReferenceKey(final Path currentPath, final Object item) {
        return idGenerator.next(item);
    }

    @Override
    protected void fireValidReference(final String referenceKey) {
        final String attributeName = getMapper().aliasForSystemAttribute("id");
        if (attributeName != null) {
            writer.addAttribute(attributeName, referenceKey.toString());
        }
    }
}
