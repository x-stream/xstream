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
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;

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
    
    private ReflectionProvider reflectionProvider;

    private transient ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public DefaultConverterLookup(JVM jvm,
                                  ReflectionProvider reflectionProvider,
                                  ImplicitCollectionMapper implicitCollectionMapper,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier) {
        this(jvm,
             reflectionProvider,
             new ReflectionConverter(classMapper, classAttributeIdentifier, "defined-in", reflectionProvider, implicitCollectionMapper),
             classMapper,
             classAttributeIdentifier);
    }

    public DefaultConverterLookup(JVM jvm,
                                  ReflectionProvider reflectionProvider,
                                  Converter defaultConverter,
                                  ClassMapper classMapper,
                                  String classAttributeIdentifier) {
        this.jvm = jvm;
        this.defaultConverter = defaultConverter;
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.reflectionProvider = reflectionProvider;
    }

    public Converter defaultConverter() {
        return defaultConverter;
    }
    
    public void changeDefaultConverter(Converter newDefaultConverter) {
        defaultConverter = newDefaultConverter;
        setupDefaults();
    }

    public Converter lookupConverterForType(Class type) {
        if (type == null) {
            return nullConverter;
        }
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        type = classMapper.defaultImplementationOf(type);
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
        registerConverter(defaultConverter);
        registerConverter(new SerializableConverter(classMapper, reflectionProvider));
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

            registerConverter(new CurrencyConverter());
            registerConverter(new RegexPatternConverter(defaultConverter()));
        }
    }

    public String getClassAttributeIdentifier() {
        return classAttributeIdentifier;
    }

    private Object readResolve() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        return this;
    }

}
