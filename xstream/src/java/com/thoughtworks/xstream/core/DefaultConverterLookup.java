package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.DefaultCollectionLookup;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.extended.ThrowableConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.FontConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.sql.Timestamp;
import java.io.File;

public class DefaultConverterLookup implements ConverterLookup, DefaultCollectionLookup {

    private LinkedList converters = new LinkedList();
    private Converter nullConverter = new NullConverter();
    private HashMap typeToConverterMap = new HashMap();
    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private Converter defaultConverter;
    private Map defaultCollections = new HashMap();
    private ClassLoader classLoader = getClass().getClassLoader();
    private JVM jvm;

    public DefaultConverterLookup(ReflectionProvider reflectionProvider,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier, JVM jvm) {
        this.jvm = jvm;
        this.defaultConverter = new ReflectionConverter(classMapper, classAttributeIdentifier, "defined-in", reflectionProvider, this);
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public DefaultConverterLookup(Converter defaultConverter,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier) {
        this.defaultConverter = defaultConverter;
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public Converter defaultConverter() {
        return defaultConverter;
    }

    public Converter lookupConverterForType(Class type) {
        if (type == null) {
            return nullConverter;
        }
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        type = classMapper.lookupDefaultType(type);
        for (Iterator iterator = converters.iterator(); iterator.hasNext();) {
            Converter converter = (Converter) iterator.next();
            if (converter.canConvert(type)) {
                typeToConverterMap.put(type, converter);
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }

    public void registerConverter(Converter converter) {
        converters.addFirst(converter);
    }

    public void setupDefaults() {
        alias("null", ClassMapper.Null.class);
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
        alias("dynamic-proxy", ClassMapper.DynamicProxy.class);
        alias("big-int", BigInteger.class);
        alias("big-decimal", BigDecimal.class);

        alias("string-buffer", StringBuffer.class);
        alias("string", String.class);
        alias("java-class", Class.class);
        alias("method", Method.class);
        alias("constructor", Constructor.class);
        alias("date", Date.class);
        alias("url", URL.class);
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

        alias("awt-color", Color.class);
        alias("awt-font", Font.class);
        alias("sql-timestamp", Timestamp.class);
        alias("file", File.class);

        if (JVM.is14()) {
            alias("linked-hash-map", jvm.loadClass("java.util.LinkedHashMap"));
            alias("linked-hash-set", jvm.loadClass("java.util.LinkedHashSet"));
        }

        registerConverter(defaultConverter);

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
        registerConverter(new BitSetConverter());
        registerConverter(new URLConverter());
        registerConverter(new BigIntegerConverter());
        registerConverter(new BigDecimalConverter());

        registerConverter(new ArrayConverter(classMapper, classAttributeIdentifier));
        registerConverter(new CharArrayConverter());
        registerConverter(new CollectionConverter(classMapper, classAttributeIdentifier));
        registerConverter(new MapConverter(classMapper, classAttributeIdentifier));
        registerConverter(new TreeMapConverter(classMapper, classAttributeIdentifier));
        registerConverter(new TreeSetConverter(classMapper, classAttributeIdentifier));
        registerConverter(new PropertiesConverter());

        registerConverter(new FileConverter());
        registerConverter(new SqlTimestampConverter());
        registerConverter(new DynamicProxyConverter(classMapper, classLoader));
        registerConverter(new JavaClassConverter(classLoader));
        registerConverter(new JavaMethodConverter());
        registerConverter(new FontConverter());
        registerConverter(new ColorConverter());

        // EncodedByteArrayConverter
        // SqlTimeConverter

        if (JVM.is14()) {
            registerConverter(new ThrowableConverter(defaultConverter()));
            registerConverter(new StackTraceElementConverter());
            alias("trace", StackTraceElement.class);
        }
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        classMapper.alias(elementName, type, defaultImplementation);
    }

    public void alias(String elementName, Class type) {
        alias(elementName, type, type);
    }

    public String getClassAttributeIdentifier() {
        return classAttributeIdentifier;
    }

    public String getDefaultCollectionField(Class type) {
        return (String) defaultCollections.get(type);
    }

    public void addDefaultCollection(Class type, String fieldName) {
        defaultCollections.put(type, fieldName);
    }
}
