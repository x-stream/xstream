package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.regex.Pattern;

/**
 * Ensures java.util.regex.Pattern is compiled upon deserialization.
 */
public class RegexPatternConverter implements Converter {

    private Converter defaultConverter;

    public RegexPatternConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
    }

    public boolean canConvert(final Class type) {
        return type.equals(Pattern.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        defaultConverter.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Pattern notCompiled = (Pattern) defaultConverter.unmarshal(reader, context);
        return Pattern.compile(notCompiled.pattern(), notCompiled.flags());
    }

}
