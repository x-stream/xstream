package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.DefaultClassMapper;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.MarshallingContextAdaptor;
import com.thoughtworks.xstream.core.UnmarshallingContextAdaptor;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class XStream {

    protected DefaultConverterLookup converterLookup = new DefaultConverterLookup();
    protected HierarchicalStreamDriver hierarchicalStreamDriver;
    protected ClassMapper classMapper;
    protected ReflectionProvider reflectionProvider;
    protected String classAttributeIdentifier;

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

    public XStream(ReflectionProvider objectFactory, ClassMapper classMapper, HierarchicalStreamDriver driver) {
        this(objectFactory, classMapper, driver, "class");
    }

    public XStream(ReflectionProvider objectFactory, ClassMapper classMapper, HierarchicalStreamDriver driver,String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.reflectionProvider = objectFactory;
        this.hierarchicalStreamDriver = driver;
        this.classAttributeIdentifier = classAttributeIdentifier;

        alias("int", Integer.class);
        alias("float", Float.class);
        alias("double", Double.class);
        alias("long", Long.class);
        alias("short", Short.class);
        alias("char", Character.class);
        alias("byte", Byte.class);
        alias("boolean", Boolean.class);
        alias("number", Number.class);
        alias("object", Object.class);

        alias("string-buffer", StringBuffer.class);
        alias("string", String.class);
        alias("java-class", Class.class);
        alias("date", Date.class);
        alias("bit-set", BitSet.class);

        alias("map", Map.class, HashMap.class);
        alias("properties", Properties.class);
        alias("list", List.class, ArrayList.class);
        alias("set", Set.class, HashSet.class);

        alias("linked-list", LinkedList.class);
        alias("vector", Vector.class);
        alias("tree-map", TreeMap.class);
        alias("tree-set", TreeSet.class);
        alias("hashtable", Hashtable.class);

        registerConverter(new ReflectionConverter(classMapper,classAttributeIdentifier, objectFactory));

        registerConverter(new IntConverter());
        registerConverter(new FloatConverter());
        registerConverter(new DoubleConverter());
        registerConverter(new LongConverter());
        registerConverter(new ShortConverter());
        registerConverter(new CharConverter());
        registerConverter(new BooleanConverter());
        registerConverter(new ByteConverter());

        registerConverter(new StringConverter());
        registerConverter(new StringBufferConverter());
        registerConverter(new DateConverter());
        registerConverter(new JavaClassConverter());
        registerConverter(new BitSetConverter());

        registerConverter(new ArrayConverter(classMapper,classAttributeIdentifier));
        registerConverter(new CharArrayConverter());
        registerConverter(new CollectionConverter(classMapper,classAttributeIdentifier));
        registerConverter(new MapConverter(classMapper,classAttributeIdentifier));
        registerConverter(new PropertiesConverter());

    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        classMapper.alias(elementName, type, defaultImplementation);
    }

    public void alias(String elementName, Class type) {
        alias(elementName, type, type);
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
        MarshallingContextAdaptor context = new MarshallingContextAdaptor(
                writer, converterLookup, classMapper);
        context.start(obj);
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
        UnmarshallingContextAdaptor context = new UnmarshallingContextAdaptor(
                root, reader, converterLookup,
                classMapper, classAttributeIdentifier);
        return context.start();
    }

    public void registerConverter(Converter converter) {
        converterLookup.registerConverter(converter);
    }

}
