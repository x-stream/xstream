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

import java.util.HashSet;
import java.util.Set;

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
    private PathTracker pathTracker = new PathTracker();
    private Path lastPath;
    private Set implicitElements = new HashSet();

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
                if (implicitElements.contains(existingReferenceKey)) {
                    throw new ReferencedImplicitElementException("Cannot reference implicit element: " + item.toString());
                }
                writer.addAttribute("reference", createReference(currentPath, existingReferenceKey));
            } else {
                Object newReferenceKey = createReferenceKey(currentPath);
                if (!currentPath.equals(lastPath)) {
                    fireValidReference(newReferenceKey);
                } else {
                    implicitElements.add(newReferenceKey);
                }
                lastPath = currentPath;
                references.associateId(item, newReferenceKey);
                converter.marshal(item, writer, this);
            }
        }
    }
    
    protected abstract String createReference(Path currentPath, Object existingReferenceKey);
    protected abstract Object createReferenceKey(Path currentPath);
    protected abstract void fireValidReference(Object referenceKey);
    
    public static class ReferencedImplicitElementException extends ConversionException {
        public ReferencedImplicitElementException(final String msg) {
            super(msg);
        }
    }
}
