package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReflectionConverter extends AbstractReflectionConverter {
    private final XStream self;

    public ReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this(mapper, reflectionProvider, null);
    }

    public ReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider, XStream xstream) {
        super(mapper, reflectionProvider);
        self = xstream;
    }

    public boolean canConvert(Class type) {
        return true;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == self) {
            throw new ConversionException("Cannot marshal the XStream instance in action");
        }
        super.marshal(source, writer, context);
    }

}
