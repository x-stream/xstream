package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.StringStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.io.path.RelativePathCalculator;

import java.util.HashMap;
import java.util.Map;

public class ReferenceByXPathUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private StringStack parentPathStack = new StringStack(16);
    private PathTracker pathTracker = new PathTracker();
    private RelativePathCalculator relativePathCalculator = new RelativePathCalculator();

    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, ClassMapper classMapper,
                                        String classAttributeIdentifier) {
        super(root, reader, converterLookup, classMapper, classAttributeIdentifier);
        this.reader = new PathTrackingReader(reader, pathTracker);
    }

    public Object convertAnother(Object parent, Class type) {
        if (parentPathStack.size() > 0) { // handles circular references
            values.put(parentPathStack.peek(), parent);
        }
        String relativePathOfReference = reader.getAttribute("reference");
        String currentPath = pathTracker.getCurrentPath();
        if (relativePathOfReference != null) {
            return values.get(relativePathCalculator.absolutePath(currentPath, relativePathOfReference));
        } else {
            parentPathStack.push(currentPath);
            Object result = super.convertAnother(parent, type);
            values.put(currentPath, result);
            parentPathStack.popSilently();
            return result;
        }
    }

}
