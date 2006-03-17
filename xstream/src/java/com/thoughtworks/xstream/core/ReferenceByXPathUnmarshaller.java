package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByXPathUnmarshaller extends AbstractReferenceUnmarshaller {

    private PathTracker pathTracker = new PathTracker();
    private final int mode;

    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, Mapper mapper, int mode) {
        super(root, reader, converterLookup, mapper);
        this.mode = mode;
        this.reader = new PathTrackingReader(reader, pathTracker);
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByXPathUnmarshaller(Object, HierarchicalStreamReader, ConverterLookup, Mapper, int)}
     */
    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, ClassMapper classMapper) {
        this(root, reader, converterLookup, classMapper, ReferenceByXPathMarshallingStrategy.RELATIVE);
    }

    protected Object getReferenceKey(String reference) {
        final Path path = new Path(reference);
        return mode == ReferenceByXPathMarshallingStrategy.RELATIVE ? pathTracker.getPath().apply(path) : path;
    }

    protected Object getCurrentReferenceKey() {
        return pathTracker.getPath();
    }

}
