/*
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Iterator;

/**
 * Abstract base class for a TreeMarshaller, that can build references.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractReferenceMarshaller extends TreeMarshaller implements MarshallingContext {

    private ObjectIdDictionary references = new ObjectIdDictionary();
    private ObjectIdDictionary implicitElements = new ObjectIdDictionary();
    private PathTracker pathTracker = new PathTracker();
    private Path lastPath;

    public AbstractReferenceMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   Mapper mapper) {
        super(writer, converterLookup, mapper);
        this.writer = new PathTrackingWriter(writer, pathTracker);
    }

    public void convert(Object item, Converter converter) {
        if (getMapper().isImmutableValueType(item.getClass())) {
            // strings, ints, dates, etc... don't bother using references.
            converter.marshal(item, writer, this);
        } else {
            final Path currentPath = pathTracker.getPath();
            Id existingReference = (Id)references.lookupId(item);
            if (existingReference != null && existingReference.getPath() != currentPath) {
                String attributeName = getMapper().aliasForSystemAttribute("reference");
                if (attributeName != null) {
                    writer.addAttribute(attributeName, createReference(currentPath, existingReference.getItem()));
                }
            } else {
                final Object newReferenceKey = existingReference == null 
                    ? createReferenceKey(currentPath, item) 
                    : existingReference.getItem();
                if (lastPath == null || !currentPath.isAncestor(lastPath)) {
                    fireValidReference(newReferenceKey);
                    lastPath = currentPath;
                    references.associateId(item, new Id(newReferenceKey, currentPath));
                }
                converter.marshal(item, writer, new ReferencingMarshallingContext() {
                    
                    public void put(Object key, Object value) {
                        AbstractReferenceMarshaller.this.put(key, value);
                    }
                    
                    public Iterator keys() {
                        return AbstractReferenceMarshaller.this.keys();
                    }
                    
                    public Object get(Object key) {
                        return AbstractReferenceMarshaller.this.get(key);
                    }
                    
                    public void convertAnother(Object nextItem, Converter converter) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem, converter);
                    }
                    
                    public void convertAnother(Object nextItem) {
                        AbstractReferenceMarshaller.this.convertAnother(nextItem);
                    }
                    
                    public void replace(Object original, Object replacement) {
                        references.associateId(replacement, new Id(newReferenceKey, currentPath));
                    }
                    
                    public Object lookupReference(Object item) {
                        Id id = (Id)references.lookupId(item);
                        return id.getItem();
                    }
                    
                    public Path currentPath() {
                        return pathTracker.getPath();
                    }

                    public void registerImplicit(Object item) {
                        if (implicitElements.containsId(item)) {
                            throw new ReferencedImplicitElementException(item, currentPath());
                        }
                        implicitElements.associateId(item, newReferenceKey);
                    }
                });
            }
        }
    }
    
    protected abstract String createReference(Path currentPath, Object existingReferenceKey);
    protected abstract Object createReferenceKey(Path currentPath, Object item);
    protected abstract void fireValidReference(Object referenceKey);
    
    private static class Id {
        private Object item;
        private Path path;
        public Id(Object item, Path path) {
            this.item = item;
            this.path = path;
        }
        protected Object getItem() {
            return this.item;
        }
        protected Path getPath() {
            return this.path;
        }
    }
    
    public static class ReferencedImplicitElementException extends ConversionException {
        public ReferencedImplicitElementException(final Object item, final Path path) {
            super("Cannot reference implicit element");
            add("implicit-element", item.toString());
            add("referencing-element", path.toString());
        }
    }
}
