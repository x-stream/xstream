package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

import java.util.IdentityHashMap;
import java.util.Map;

public class ReferenceByXPathMarshaller extends TreeMarshaller {

    private PathTracker pathTracker = new PathTracker();
    private Map references = new IdentityHashMap();

    public ReferenceByXPathMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup, ClassMapper classMapper) {
        super(writer, converterLookup, classMapper);
        this.writer = new PathTrackingWriter(writer, pathTracker);
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());

        if (isImmutableBasicType(converter)) {
            // strings, ints, dates, etc... don't bother using references.
            converter.marshal(item, writer, this);
        } else {
            String pathOfExistingReference = (String) references.get(item);
            if (pathOfExistingReference != null) {
                writer.addAttribute("reference", pathOfExistingReference);
            } else {
                String currentPath = pathTracker.getCurrentPath();
                references.put(item, currentPath);
                converter.marshal(item, writer, this);
            }
        }
    }

    private boolean isImmutableBasicType(Converter converter) {
        return converter instanceof AbstractBasicConverter;
    }
}
