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

import java.util.Iterator;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Abstract base class for a TreeMarshaller, that can build references.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractReferenceMarshaller<R> extends TreeMarshaller {

    private final ObjectIdDictionary<Id<R>> references = new ObjectIdDictionary<>();
    private final ObjectIdDictionary<Object> implicitElements = new ObjectIdDictionary<>();
    private final PathTracker pathTracker = new PathTracker();
    private Path lastPath;

    public AbstractReferenceMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper) {
        super(writer, converterLookup, mapper);
        this.writer = new PathTrackingWriter(writer, pathTracker);
    }

    @Override
    public void convert(final Object item, final Converter converter) {
        if (getMapper().isImmutableValueType(item.getClass())) {
            // strings, ints, dates, etc... don't bother using references.
            converter.marshal(item, writer, this);
        } else {
            final Path currentPath = pathTracker.getPath();
            final Id<R> existingReference = references.lookupId(item);
            if (existingReference != null && existingReference.getPath() != currentPath) {
                final String attributeName = getMapper().aliasForSystemAttribute("reference");
                if (attributeName != null) {
                    writer.addAttribute(attributeName, createReference(currentPath, existingReference.getItem()));
                }
            } else {
                final R newReferenceKey = existingReference == null
                    ? createReferenceKey(currentPath, item)
                    : existingReference.getItem();
                if (lastPath == null || !currentPath.isAncestor(lastPath)) {
                    fireValidReference(newReferenceKey);
                    lastPath = currentPath;
                    references.associateId(item, new Id<>(newReferenceKey, currentPath));
                }
                converter.marshal(item, writer, new ReferencingMarshallingContext<R>() {

                    @Override
                    public void put(final Object key, final Object value) {
                        AbstractReferenceMarshaller.this.put(key, value);
                    }

                    @Override
                    public Iterator<Object> keys() {
                        return AbstractReferenceMarshaller.this.keys();
                    }

                    @Override
                    public Object get(final Object key) {
                        return AbstractReferenceMarshaller.this.get(key);
                    }

                    @Override
                    public void convertAnother(final Object nextItem, final Converter converter) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem, converter);
                    }

                    @Override
                    public void convertAnother(final Object nextItem) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem);
                    }

                    @Override
                    public void replace(final Object original, final Object replacement) {
                        references.associateId(replacement, new Id<>(newReferenceKey, currentPath));
                    }

                    @Override
                    public R lookupReference(final Object item) {
                        final Id<R> id = references.lookupId(item);
                        return id.getItem();
                    }

                    /**
                     * @deprecated As of 1.4.2
                     */
                    @Deprecated
                    @Override
                    public Path currentPath() {
                        return pathTracker.getPath();
                    }

                    @Override
                    public void registerImplicit(final Object item) {
                        if (implicitElements.containsId(item)) {
                            throw new ReferencedImplicitElementException(item, currentPath);
                        }
                        implicitElements.associateId(item, newReferenceKey);
                    }
                });
            }
        }
    }

    protected abstract String createReference(Path currentPath, R existingReferenceKey);

    protected abstract R createReferenceKey(Path currentPath, Object item);

    protected abstract void fireValidReference(R referenceKey);

    private static class Id<R> {
        private final R item;
        private final Path path;

        public Id(final R item, final Path path) {
            this.item = item;
            this.path = path;
        }

        protected R getItem() {
            return item;
        }

        protected Path getPath() {
            return path;
        }
    }

    public static class ReferencedImplicitElementException extends ConversionException {
        private static final long serialVersionUID = 10200L;

        public ReferencedImplicitElementException(final Object item, final Path path) {
            super("Cannot reference implicit element");
            add("implicit-element", item.toString());
            add("referencing-element", path.toString());
        }
    }
}
