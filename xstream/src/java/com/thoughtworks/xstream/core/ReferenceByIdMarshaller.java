package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.IdentityHashMap;
import java.util.Map;

public class ReferenceByIdMarshaller extends TreeMarshaller {

    private Map references = new IdentityHashMap();
    private IDGenerator idGenerator;

    public static interface IDGenerator {
        String next();
    }
    
    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   ClassMapper classMapper,
                                   IDGenerator idGenerator) {
        super(writer, converterLookup, classMapper);
        this.idGenerator = idGenerator;
    }

    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   ClassMapper classMapper) {
        this(writer, converterLookup, classMapper, new SequenceGenerator(1));
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());

        if (isImmutableBasicType(converter)) {
            // strings, ints, dates, etc... don't bother using references.
            converter.marshal(item, writer, this);
        } else {
            String idOfExistingReference = (String)references.get(item);
            if (idOfExistingReference != null) {
                writer.addAttribute("reference", idOfExistingReference);
            } else {
                String newId = idGenerator.next();
                writer.addAttribute("id", newId);
                references.put(item, newId);
                converter.marshal(item, writer, this);
            }
        }
    }

    private boolean isImmutableBasicType(Converter converter) {
        return converter instanceof AbstractBasicConverter;
    }

}
