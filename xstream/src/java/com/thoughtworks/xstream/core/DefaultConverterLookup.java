package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ImplicitCollectionMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.basic.BigDecimalConverter;
import com.thoughtworks.xstream.converters.basic.BigIntegerConverter;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.ByteConverter;
import com.thoughtworks.xstream.converters.basic.CharConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.basic.ShortConverter;
import com.thoughtworks.xstream.converters.basic.StringBufferConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.basic.URLConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.BitSetConverter;
import com.thoughtworks.xstream.converters.collections.CharArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.collections.PropertiesConverter;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.converters.collections.TreeSetConverter;
import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.CurrencyConverter;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.FontConverter;
import com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.RegexPatternConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.ThrowableConverter;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class DefaultConverterLookup implements ConverterLookup {

    private ArrayList converters = new ArrayList();
    private Converter nullConverter = new NullConverter();
    private HashMap typeToConverterMap = new HashMap();
    private ClassMapper classMapper;
    private String classAttributeIdentifier;
    private Converter defaultConverter;
    private JVM jvm;

    private transient ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public DefaultConverterLookup(ReflectionProvider reflectionProvider,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier,
                                  JVM jvm,
                                  ImplicitCollectionMapper implicitCollectionMapper) {
        this.jvm = jvm;
        this.defaultConverter = new ReflectionConverter(classMapper, classAttributeIdentifier, "defined-in", reflectionProvider, implicitCollectionMapper);
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
        int size = converters.size();
        for (int i = size - 1; i >= 0; i--) {
            Converter converter = (Converter) converters.get(i);
            if (converter.canConvert(type)) {
                typeToConverterMap.put(type, converter);
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }

    public void registerConverter(Converter converter) {
        converters.add(converter);
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
        alias("entry", Map.Entry.class);
        alias("properties", Properties.class);
        alias("list", List.class, ArrayList.class);
        alias("set", Set.class, HashSet.class);

        alias("linked-list", LinkedList.class);
        alias("vector", Vector.class);
        alias("tree-map", TreeMap.class);
        alias("tree-set", TreeSet.class);
        alias("hashtable", Hashtable.class);

        // Instantiating these two classes starts the AWT system, which is undesirable. Calling loadClass ensures
        // a reference to the class is found but they are not instantiated.
        alias("awt-color", jvm.loadClass("java.awt.Color"));
        alias("awt-font", jvm.loadClass("java.awt.Font"));

        alias("sql-timestamp", Timestamp.class);
        alias("sql-time", Time.class);
        alias("sql-date", java.sql.Date.class);
        alias("file", File.class);
        alias("locale", Locale.class);
        alias("gregorian-calendar", Calendar.class, GregorianCalendar.class);

        if (JVM.is14()) {
            alias("linked-hash-map", jvm.loadClass("java.util.LinkedHashMap"));
            alias("linked-hash-set", jvm.loadClass("java.util.LinkedHashSet"));
        }

        registerConverter(defaultConverter);
        registerConverter(new ExternalizableConverter(classMapper));

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
        registerConverter(new EncodedByteArrayConverter());

        registerConverter(new FileConverter());
        registerConverter(new SqlTimestampConverter());
        registerConverter(new SqlTimeConverter());
        registerConverter(new SqlDateConverter());
        registerConverter(new DynamicProxyConverter(classMapper, classLoader));
        registerConverter(new JavaClassConverter(classLoader));
        registerConverter(new JavaMethodConverter());
        registerConverter(new FontConverter());
        registerConverter(new ColorConverter());
        registerConverter(new LocaleConverter());
        registerConverter(new GregorianCalendarConverter());

        if (JVM.is14()) {
            registerConverter(new ThrowableConverter(defaultConverter()));
            registerConverter(new StackTraceElementConverter());
            alias("trace", jvm.loadClass("java.lang.StackTraceElement"));

            registerConverter(new CurrencyConverter());
            alias("currency", jvm.loadClass("java.util.Currency"));
            registerConverter(new RegexPatternConverter(defaultConverter()));
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

    private Object readResolve() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        return this;
    }

}
