package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class XStream {

    protected ConverterLookup converterLookup = new DefaultConverterLookup();
    protected HierarchicalStreamDriver hierarchicalStreamDriver;
    protected ClassMapper classMapper;
    protected ReflectionProvider reflectionProvider;
    protected String classAttributeIdentifier;

    public XStream() {
        this(new Sun14ReflectionProvider(), new DefaultClassMapper(new DefaultNameMapper()), new DomDriver());
    }

    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(new Sun14ReflectionProvider(), new DefaultClassMapper(new DefaultNameMapper()), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, new DefaultClassMapper(new DefaultNameMapper()), new DomDriver());
    }

    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, new DefaultClassMapper(new DefaultNameMapper()), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider objectFactory, ClassMapper classMapper, HierarchicalStreamDriver xmlReaderDriver) {
        this(objectFactory, classMapper, xmlReaderDriver, "class");
    }

    public XStream(ReflectionProvider objectFactory, ClassMapper classMapper, HierarchicalStreamDriver xmlReaderDriver,String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.reflectionProvider = objectFactory;
        this.hierarchicalStreamDriver = xmlReaderDriver;
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
        toXML(obj, writer);
        return stringWriter.toString();
    }

    public void toXML(Object obj, HierarchicalStreamWriter writer) {
        Converter rootConverter = converterLookup.lookupConverterForType(obj.getClass());
        writer.startNode(classMapper.lookupName(obj.getClass()));
        MarshallingContextAdaptor context = new MarshallingContextAdaptor(writer, converterLookup);
        rootConverter.toXML(obj, writer, context);
        writer.endNode();
    }

    public Object fromXML(String xml) {
        return fromXML(hierarchicalStreamDriver.createReader(xml), null);
    }

    public Object fromXML(HierarchicalStreamReader xmlReader) {
        return fromXML(xmlReader, null);
    }

    public Object fromXML(HierarchicalStreamReader xmlReader, Object root) {
        String classAttribute = xmlReader.getAttribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(xmlReader.getNodeName());
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        UnmarshallingContextAdaptor context = new UnmarshallingContextAdaptor(root, xmlReader, converterLookup);
        return context.convertAnother(type);
    }

    public void registerConverter(Converter converter) {
        converterLookup.registerConverter(converter);
    }

}
