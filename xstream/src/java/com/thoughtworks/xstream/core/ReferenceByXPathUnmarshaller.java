package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByXPathUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private String lastPath;
    private PathTracker pathTracker = new PathTracker();

    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, ClassMapper classMapper,
                                        String classAttributeIdentifier) {
        super(root, reader, converterLookup, classMapper, classAttributeIdentifier);
        this.reader = new PathTrackingReader(reader, pathTracker);
    }

    public Object convertAnother(Class type) {
        throw new UnsupportedOperationException();
    }

    public Object convertAnother(Object current, Class type) {
        if (lastPath != null) { // handles circular references
            values.put(lastPath, current);
        }
        String reference = reader.getAttribute("reference");
        if (reference != null) {
            return values.get(reference);
        } else {
            lastPath = pathTracker.getCurrentPath();
            return super.convertAnother(current, type);
        }
    }

}
