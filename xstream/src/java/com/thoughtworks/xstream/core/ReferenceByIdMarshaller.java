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
        super(new IDCountingStreamWriter(writer), converterLookup, mapper);
        this.idGenerator = idGenerator;
    }

    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   Mapper mapper) {
        this(new IDCountingStreamWriter(writer), converterLookup, mapper, new SequenceGenerator(1));
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByIdMarshaller(HierarchicalStreamWriter, ConverterLookup, Mapper, IDGenerator)}
     */
    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer,
                                   ConverterLookup converterLookup,
                                   ClassMapper classMapper,
                                   IDGenerator idGenerator) {
        super(new IDCountingStreamWriter(writer), converterLookup, classMapper);
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

    public void convert(Object item, Converter converter) {
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

    // TODO: Attempt for XSTR-276, but we must find generic solution ... see XSTR-283
    private static class IDCountingStreamWriter implements HierarchicalStreamWriter {

        private final HierarchicalStreamWriter wrapped;
        private int counter;

        private IDCountingStreamWriter(HierarchicalStreamWriter wrapped) {
            this.wrapped = wrapped;
        }

        public void addAttribute(String name, String value) {
            if (name.equals("id")) {
               if (counter == 1) {
                   //name = "id-implicit";
               } else {
                   counter++;
               }
            }
            this.wrapped.addAttribute(name, value);
        }

        public void close() {
            this.wrapped.close();
        }

        public void endNode() {
            this.wrapped.endNode();
            counter = 0;
        }

        public void flush() {
            this.wrapped.flush();
        }

        public void setValue(String text) {
            this.wrapped.setValue(text);
        }

        public void startNode(String name) {
            counter = 0;
            this.wrapped.startNode(name);
        }

        public HierarchicalStreamWriter underlyingWriter() {
            return this.wrapped.underlyingWriter();
        }

    }
}
