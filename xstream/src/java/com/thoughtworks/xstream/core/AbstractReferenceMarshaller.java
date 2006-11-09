package com.thoughtworks.xstream.core;

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
 * Abstract base class for a TreeMarshaller, that can build refrences.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractReferenceMarshaller extends TreeMarshaller {

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
            Path currentPath = pathTracker.getPath();
            Object existingReferenceKey = references.lookupId(item);
            if (existingReferenceKey != null) {
                writer.addAttribute(getMapper().aliasForAttribute("reference"), createReference(currentPath, existingReferenceKey));
            } else if (implicitElements.lookupId(item) != null) {
                throw new ReferencedImplicitElementException(item, currentPath);
            } else {
                Object newReferenceKey = createReferenceKey(currentPath);
                if (lastPath == null || !currentPath.isAncestor(lastPath)) {
                    fireValidReference(newReferenceKey);
                    lastPath = currentPath;
                    references.associateId(item, newReferenceKey);
                } else {
                    implicitElements.associateId(item, newReferenceKey);
                }
                converter.marshal(item, writer, this);
            }
        }
    }
    
    protected abstract String createReference(Path currentPath, Object existingReferenceKey);
    protected abstract Object createReferenceKey(Path currentPath);
    protected abstract void fireValidReference(Object referenceKey);
    
    public static class ReferencedImplicitElementException extends ConversionException {
        /**
         * @deprecated since 1.2.1
         */
        public ReferencedImplicitElementException(final String msg) {
            super(msg);
        }
        public ReferencedImplicitElementException(final Object item, final Path path) {
            super("Cannot reference implicit element");
            add("implicit-element", item.toString());
            add("referencing-element", path.toString());
        }
    }
}
