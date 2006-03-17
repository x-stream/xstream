package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByXPathMarshaller extends AbstractReferenceMarshaller {

    private final int mode;

    public ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper, int mode) {
        super(writer, converterLookup, mapper);
        this.mode = mode;
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByXPathMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper, int)}
     */
    public ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, ClassMapper classMapper) {
        this(writer, converterLookup, classMapper, ReferenceByXPathMarshallingStrategy.RELATIVE);
    }

    protected String createReference(Path currentPath, Object existingReferenceKey) {
        return (mode == ReferenceByXPathMarshallingStrategy.RELATIVE ? currentPath.relativeTo((Path)existingReferenceKey) : existingReferenceKey).toString();
    }

    protected Object createReferenceKey(Path currentPath) {
        return currentPath;
    }

    protected void fireValidReference(Object referenceKey) {
        // nothing to do
    }
}
