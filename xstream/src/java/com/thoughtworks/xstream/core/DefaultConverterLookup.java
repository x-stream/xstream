package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

public class DefaultConverterLookup implements ConverterLookup {

    private LinkedList converters = new LinkedList();
    private Converter nullConverter = new NullConverter();
    private HashMap typeToConverterMap = new HashMap();
    private ReflectionProvider reflectionProvider;
    private ClassMapper classMapper;
    private String classAttributeIdentifier;

    public DefaultConverterLookup(ReflectionProvider reflectionProvider,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier) {
        this.reflectionProvider = reflectionProvider;
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
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
        alias("linked-hash-map", LinkedHashMap.class);
        alias("linked-hash-set", LinkedHashSet.class);
        alias("tree-set", TreeSet.class);
        alias("hashtable", Hashtable.class);

        registerConverter(new ReflectionConverter(classMapper, classAttributeIdentifier, reflectionProvider));

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
        registerConverter(new JavaMethodConverter());
        registerConverter(new BitSetConverter());
        registerConverter(new URLConverter());
        registerConverter(new BigIntegerConverter());
        registerConverter(new BigDecimalConverter());

        registerConverter(new ArrayConverter(classMapper, classAttributeIdentifier));
        registerConverter(new CharArrayConverter());
        registerConverter(new CollectionConverter(classMapper, classAttributeIdentifier));
        registerConverter(new MapConverter(classMapper, classAttributeIdentifier));
        registerConverter(new PropertiesConverter());
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

}
