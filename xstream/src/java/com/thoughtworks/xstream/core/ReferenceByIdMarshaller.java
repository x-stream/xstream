package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.IdentityHashMap;
import java.util.Map;

public class ReferenceByIdMarshaller extends TreeMarshaller {

    private Map references = new IdentityHashMap();
    private IdGenerator idGenerator = new IdGenerator();
    private Is is = new Is();

    public ReferenceByIdMarshaller(HierarchicalStreamWriter writer, ConverterLookup converterLookup,
                                     ClassMapper classMapper) {
        super(writer, converterLookup, classMapper);
    }

    public void convertAnother(Object item) {
        if (is.is(item.getClass())) {
            if (references.containsKey(item)) {
                writer.addAttribute("reference", (String)references.get(item));
            } else {
                String id = idGenerator.next();
                writer.addAttribute("id", id);
                references.put(item, id);
                super.convertAnother(item);
            }
        } else {
            super.convertAnother(item);
        }
    }

    private static class Is {
        private boolean is(Class type) {
            return type != String.class;
        }
    }

    private static class IdGenerator {
        private int id = 1;
        public String next() {
            return String.valueOf(id++);
        }
    }

}
