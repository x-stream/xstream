package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByIdMarshaller extends TreeMarshaller {

    private ObjectIdDictionary references = new ObjectIdDictionary();
    private IDGenerator idGenerator;

    public static interface IDGenerator {
        String next();
    }

    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   Mapper mapper,
                                   IDGenerator idGenerator) {
        super(writer, converterLookup, mapper);
        this.idGenerator = idGenerator;
    }

    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   Mapper mapper) {
        this(writer, converterLookup, mapper, new SequenceGenerator(1));
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByIdMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper, IDGenerator)}
     */
    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   ClassMapper classMapper,
                                   IDGenerator idGenerator) {
        super(writer, converterLookup, classMapper);
        this.idGenerator = idGenerator;
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByIdMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper)}
     */
    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   ClassMapper classMapper) {
        this(writer, converterLookup, classMapper, new SequenceGenerator(1));
    }

    public void convertAnother(Object item) {
        Converter converter = converterLookup.lookupConverterForType(item.getClass());

        if (getMapper().isImmutableValueType(item.getClass())) {
            // strings, ints, dates, etc... don't bother using references.
            converter.marshal(item, writer, this);
        } else {
            Object idOfExistingReference = references.lookupId(item);
            if (idOfExistingReference != null) {
                writer.addAttribute("reference", idOfExistingReference.toString());
            } else {
                String newId = idGenerator.next();
                writer.addAttribute("id", newId);
                references.associateId(item, newId);
                converter.marshal(item, writer, this);
            }
        }
    }

}
