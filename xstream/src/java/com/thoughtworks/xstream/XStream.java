package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XStream {

    private HierarchicalStreamDriver hierarchicalStreamDriver;
    private MarshallingStrategy marshallingStrategy;
    private ClassMapper classMapper;
    private DefaultConverterLookup converterLookup;

    public XStream() {
        this(new Sun14ReflectionProvider(), new DefaultClassMapper(), new DomDriver());
    }

    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(new Sun14ReflectionProvider(), new DefaultClassMapper(), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, new DefaultClassMapper(), new DomDriver());
    }

    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, new DefaultClassMapper(), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver) {
        this(reflectionProvider, classMapper, driver, "class");
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver, String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.hierarchicalStreamDriver = driver;
        this.marshallingStrategy = new TreeMarshallingStrategy();
        converterLookup = new DefaultConverterLookup(reflectionProvider, classMapper, classAttributeIdentifier);
        converterLookup.setupDefaults();
    }

    public MarshallingStrategy getMarshallingStrategy() {
        return marshallingStrategy;
    }

    public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    public String toXML(Object obj) {
        Writer stringWriter = new StringWriter();
        HierarchicalStreamWriter writer = new PrettyPrintWriter(stringWriter);
        marshal(obj, writer);
        return stringWriter.toString();
    }

    public void toXML(Object obj, Writer writer) {
        marshal(obj, new PrettyPrintWriter(writer));
    }

    public void marshal(Object obj, HierarchicalStreamWriter writer) {
        marshallingStrategy.marshal(writer, obj, converterLookup, classMapper);
    }

    public Object fromXML(String xml) {
        return fromXML(new StringReader(xml));
    }

    public Object fromXML(Reader xml) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), null);
    }

    public Object unmarshal(HierarchicalStreamReader reader) {
        return unmarshal(reader, null);
    }

    public Object unmarshal(HierarchicalStreamReader reader, Object root) {
        return marshallingStrategy.unmarshal(root, reader, converterLookup, classMapper);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        converterLookup.alias(elementName, type, defaultImplementation);
    }

    public void alias(String elementName, Class type) {
        converterLookup.alias(elementName, type, type);
    }

    public void registerConverter(Converter converter) {
        converterLookup.registerConverter(converter);
    }

    public ClassMapper getClassMapper() {
        return classMapper;
    }
}
