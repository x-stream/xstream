package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.io.path.RelativePathCalculator;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByXPathUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private String lastPath;
    private PathTracker pathTracker = new PathTracker();
    private RelativePathCalculator relativePathCalculator = new RelativePathCalculator();

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
        String relativePathOfReference = reader.getAttribute("reference");
        String currentPath = pathTracker.getCurrentPath();
        if (relativePathOfReference != null) {
            return values.get(relativePathCalculator.absolutePath(currentPath, relativePathOfReference));
        } else {
            lastPath = currentPath;
            return super.convertAnother(current, type);
        }
    }

}
