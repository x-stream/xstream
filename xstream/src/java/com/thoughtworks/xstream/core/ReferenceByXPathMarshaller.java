package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByXPathMarshaller extends AbstractReferenceMarshaller {

    public ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
        super(writer, converterLookup, mapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByXPathMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper)}
     */
    public ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, ClassMapper classMapper) {
        super(writer, converterLookup, classMapper);
    }

    protected String createReference(Path currentPath, Object existingReferenceKey) {
        return currentPath.relativeTo((Path)existingReferenceKey).toString();
    }

    protected Object createReferenceKey(Path currentPath) {
        return currentPath;
    }

    protected void fireValidReference(Object referenceKey) {
        // nothing to do
    }
}
