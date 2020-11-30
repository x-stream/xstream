/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
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
import com.thoughtworks.xstream.converters.basic.URIConverter;
import com.thoughtworks.xstream.converters.basic.URLConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.BitSetConverter;
import com.thoughtworks.xstream.converters.collections.CharArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.collections.PropertiesConverter;
import com.thoughtworks.xstream.converters.collections.SingletonCollectionConverter;
import com.thoughtworks.xstream.converters.collections.SingletonMapConverter;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.converters.collections.TreeSetConverter;
import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.FontConverter;
import com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaFieldConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.LookAndFeelConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.core.ReferenceByIdMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshallingStrategy;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StatefulWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.AnnotationConfiguration;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.ElementIgnoringMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LocalConversionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.mapper.PackageAliasingMapper;
import com.thoughtworks.xstream.mapper.SecurityMapper;
import com.thoughtworks.xstream.mapper.SystemAttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.XStream11XmlFriendlyMapper;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import com.thoughtworks.xstream.security.InterfaceTypePermission;
import com.thoughtworks.xstream.security.NoPermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.RegExpTypePermission;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import com.thoughtworks.xstream.security.TypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;


/**
 * Simple facade to XStream library, a Java-XML serialization tool.
 * <p>
 * <hr>
 * <b>Example</b><blockquote>
 *
 * <pre>
 * XStream xstream = new XStream();
 * String xml = xstream.toXML(myObject); // serialize to XML
 * Object myObject2 = xstream.fromXML(xml); // deserialize from XML
 * </pre>
 *
 * </blockquote>
 * <hr>
 * <h3>Aliasing classes</h3>
 * <p>
 * To create shorter XML, you can specify aliases for classes using the <code>alias()</code> method. For example, you
 * can shorten all occurrences of element <code>&lt;com.blah.MyThing&gt;</code> to <code>&lt;my-thing&gt;</code> by
 * registering an alias for the class.
 * <p>
 * <hr>
 * <blockquote>
 *
 * <pre>
 * xstream.alias(&quot;my-thing&quot;, MyThing.class);
 * </pre>
 *
 * </blockquote>
 * <hr>
 * <h3>Converters</h3>
 * <p>
 * XStream contains a map of {@link com.thoughtworks.xstream.converters.Converter} instances, each of which acts as a
 * strategy for converting a particular type of class to XML and back again. Out of the box, XStream contains converters
 * for most basic types (String, Date, int, boolean, etc) and collections (Map, List, Set, Properties, etc). For other
 * objects reflection is used to serialize each field recursively.
 * </p>
 * <p>
 * Extra converters can be registered using the <code>registerConverter()</code> method. Some non-standard converters
 * are supplied in the {@link com.thoughtworks.xstream.converters.extended} package and you can create your own by
 * implementing the {@link com.thoughtworks.xstream.converters.Converter} interface.
 * </p>
 * <p>
 * <hr>
 * <b>Example</b><blockquote>
 *
 * <pre>
 * xstream.registerConverter(new SqlTimestampConverter());
 * xstream.registerConverter(new DynamicProxyConverter());
 * </pre>
 *
 * </blockquote>
 * <hr>
 * <p>
 * The converters can be registered with an explicit priority. By default they are registered with
 * XStream.PRIORITY_NORMAL. Converters of same priority will be used in the reverse sequence they have been registered.
 * The default converter, i.e. the converter which will be used if no other registered converter is suitable, can be
 * registered with priority XStream.PRIORITY_VERY_LOW. XStream uses by default the
 * {@link com.thoughtworks.xstream.converters.reflection.ReflectionConverter} as the fallback converter.
 * </p>
 * <p>
 * <hr>
 * <b>Example</b><blockquote>
 *
 * <pre>
 * xstream.registerConverter(new CustomDefaultConverter(), XStream.PRIORITY_VERY_LOW);
 * </pre>
 *
 * </blockquote>
 * <hr>
 * <h3>Object graphs</h3>
 * <p>
 * XStream has support for object graphs; a deserialized object graph will keep references intact, including circular
 * references.
 * </p>
 * <p>
 * XStream can signify references in XML using either relative/absolute XPath or IDs. The mode can be changed using
 * <code>setMode()</code>:
 * </p>
 * <table border='1'>
 * <caption></caption>
 * <tr>
 * <td><code>xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);</code></td>
 * <td><i>(Default)</i> Uses XPath relative references to signify duplicate references. This produces XML with the least
 * clutter.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);</code></td>
 * <td>Uses XPath absolute references to signify duplicate references. This produces XML with the least clutter.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.SINGLE_NODE_XPATH_RELATIVE_REFERENCES);</code></td>
 * <td>Uses XPath relative references to signify duplicate references. The XPath expression ensures that a single node
 * only is selected always.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES);</code></td>
 * <td>Uses XPath absolute references to signify duplicate references. The XPath expression ensures that a single node
 * only is selected always.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.ID_REFERENCES);</code></td>
 * <td>Uses ID references to signify duplicate references. In some scenarios, such as when using hand-written XML, this
 * is easier to work with.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.NO_REFERENCES);</code></td>
 * <td>This disables object graph support and treats the object structure like a tree. Duplicate references are treated
 * as two separate objects and circular references cause an exception. This is slightly faster and uses less memory than
 * the other two modes.</td>
 * </tr>
 * </table>
 * <h3>Thread safety</h3>
 * <p>
 * The XStream instance is thread-safe. That is, once the XStream instance has been created and configured, it may be
 * shared across multiple threads allowing objects to be serialized/deserialized concurrently. <em>Note, that this only
 * applies if annotations are not auto-detected on-the-fly.</em>
 * </p>
 * <h3>Implicit collections</h3>
 * <p>
 * To avoid the need for special tags for collections, you can define implicit collections using one of the
 * <code>addImplicitCollection</code> methods.
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Guilherme Silveira
 */
public class XStream {

    // CAUTION: The sequence of the fields is intentional for an optimal XML output of a
    // self-serialization!
    private ReflectionProvider reflectionProvider;
    private HierarchicalStreamDriver hierarchicalStreamDriver;
    private ClassLoaderReference classLoaderReference;
    private MarshallingStrategy marshallingStrategy;
    private ConverterLookup converterLookup;
    private ConverterRegistry converterRegistry;
    private Mapper mapper;

    private PackageAliasingMapper packageAliasingMapper;
    private ClassAliasingMapper classAliasingMapper;
    private FieldAliasingMapper fieldAliasingMapper;
    private ElementIgnoringMapper elementIgnoringMapper;
    private AttributeAliasingMapper attributeAliasingMapper;
    private SystemAttributeAliasingMapper systemAttributeAliasingMapper;
    private AttributeMapper attributeMapper;
    private DefaultImplementationsMapper defaultImplementationsMapper;
    private ImmutableTypesMapper immutableTypesMapper;
    private ImplicitCollectionMapper implicitCollectionMapper;
    private LocalConversionMapper localConversionMapper;
    private SecurityMapper securityMapper;
    private AnnotationConfiguration annotationConfiguration;

    private transient boolean securityInitialized;
    private transient boolean securityWarningGiven;

    public static final int NO_REFERENCES = 1001;
    public static final int ID_REFERENCES = 1002;
    public static final int XPATH_RELATIVE_REFERENCES = 1003;
    public static final int XPATH_ABSOLUTE_REFERENCES = 1004;
    public static final int SINGLE_NODE_XPATH_RELATIVE_REFERENCES = 1005;
    public static final int SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES = 1006;

    public static final int PRIORITY_VERY_HIGH = 10000;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_LOW = -10;
    public static final int PRIORITY_VERY_LOW = -20;

    private static final String ANNOTATION_MAPPER_TYPE = "com.thoughtworks.xstream.mapper.AnnotationMapper";
    private static final Pattern IGNORE_ALL = Pattern.compile(".*");
    private static final Pattern LAZY_ITERATORS = Pattern.compile(".*\\$LazyIterator");
    private static final Pattern JAVAX_CRYPTO = Pattern.compile("javax\\.crypto\\..*");

    /**
     * Constructs a default XStream.
     * <p>
     * The instance will use the {@link XppDriver} as default and tries to determine the best match for the
     * {@link ReflectionProvider} on its own.
     * </p>
     *
     * @throws InitializationException in case of an initialization problem
     */
    public XStream() {
        this(null, (Mapper)null, new XppDriver());
    }

    /**
     * Constructs an XStream with a special {@link ReflectionProvider}.
     * <p>
     * The instance will use the {@link XppDriver} as default.
     * </p>
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching reflection provider
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, (Mapper)null, new XppDriver());
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}.
     * <p>
     * The instance will tries to determine the best match for the {@link ReflectionProvider} on its own.
     * </p>
     *
     * @param hierarchicalStreamDriver the driver instance
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(null, (Mapper)null, hierarchicalStreamDriver);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver} and {@link ReflectionProvider}.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param hierarchicalStreamDriver the driver instance
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, (Mapper)null, hierarchicalStreamDriver);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider} and a prepared
     * {@link Mapper} chain.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param mapper the instance with the {@link Mapper} chain or <em>null</em> for the default chain
     * @param driver the driver instance
     * @throws InitializationException in case of an initialization problem
     * @deprecated As of 1.3, use {@link #XStream(ReflectionProvider, HierarchicalStreamDriver, ClassLoader, Mapper)}
     *             instead
     */
    public XStream(ReflectionProvider reflectionProvider, Mapper mapper, HierarchicalStreamDriver driver) {
        this(reflectionProvider, driver, new CompositeClassLoader(), mapper);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider} and a
     * {@link ClassLoaderReference}.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoaderReference the reference to the {@link ClassLoader} to use
     * @throws InitializationException in case of an initialization problem
     * @since 1.4.5
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
            ClassLoaderReference classLoaderReference) {
        this(reflectionProvider, driver, classLoaderReference, null);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider} and the
     * {@link ClassLoader} to use.
     * 
     * @throws InitializationException in case of an initialization problem
     * @since 1.3
     * @deprecated As of 1.4.5 use {@link #XStream(ReflectionProvider, HierarchicalStreamDriver, ClassLoaderReference)}
     */
    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader) {
        this(reflectionProvider, driver, classLoader, null);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider}, a prepared
     * {@link Mapper} chain and the {@link ClassLoader} to use.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoader the {@link ClassLoader} to use
     * @param mapper the instance with the {@link Mapper} chain or <em>null</em> for the default chain
     * @throws InitializationException in case of an initialization problem
     * @since 1.3
     * @deprecated As of 1.4.5 use
     *             {@link #XStream(ReflectionProvider, HierarchicalStreamDriver, ClassLoaderReference, Mapper)}
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader,
            Mapper mapper) {
        this(reflectionProvider, driver, new ClassLoaderReference(classLoader), mapper, new DefaultConverterLookup());
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider}, a prepared
     * {@link Mapper} chain and the {@link ClassLoaderReference}.
     * <p>
     * The {@link ClassLoaderReference} should also be used for the {@link Mapper} chain.
     * </p>
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoaderReference the reference to the {@link ClassLoader} to use
     * @param mapper the instance with the {@link Mapper} chain or <em>null</em> for the default chain
     * @throws InitializationException in case of an initialization problem
     * @since 1.4.5
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
            ClassLoaderReference classLoaderReference, Mapper mapper) {
        this(reflectionProvider, driver, classLoaderReference, mapper, new DefaultConverterLookup());
    }

    private XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoader,
            Mapper mapper, final DefaultConverterLookup defaultConverterLookup) {
        this(reflectionProvider, driver, classLoader, mapper, new ConverterLookup() {
            public Converter lookupConverterForType(Class type) {
                return defaultConverterLookup.lookupConverterForType(type);
            }
        }, new ConverterRegistry() {
            public void registerConverter(Converter converter, int priority) {
                defaultConverterLookup.registerConverter(converter, priority);
            }
        });
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider}, a prepared
     * {@link Mapper} chain, the {@link ClassLoaderReference} and an own {@link ConverterLookup} and
     * {@link ConverterRegistry}.
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoader the {@link ClassLoader} to use
     * @param mapper the instance with the {@link Mapper} chain or <em>null</em> for the default chain
     * @param converterLookup the instance that is used to lookup the converters
     * @param converterRegistry an instance to manage the converter instances
     * @throws InitializationException in case of an initialization problem
     * @since 1.3
     * @deprecated As of 1.4.5 use
     *             {@link #XStream(ReflectionProvider, HierarchicalStreamDriver, ClassLoaderReference, Mapper, ConverterLookup, ConverterRegistry)}
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader,
            Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        this(reflectionProvider, driver, new ClassLoaderReference(classLoader), mapper, converterLookup,
            converterRegistry);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}, {@link ReflectionProvider}, a prepared
     * {@link Mapper} chain, the {@link ClassLoaderReference} and an own {@link ConverterLookup} and
     * {@link ConverterRegistry}.
     * <p>
     * The ClassLoaderReference should also be used for the Mapper chain. The ConverterLookup should access the
     * ConverterRegistry if you intent to register {@link Converter} instances with XStream facade or you are using
     * annotations.
     * </p>
     * 
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param driver the driver instance
     * @param classLoaderReference the reference to the {@link ClassLoader} to use
     * @param mapper the instance with the {@link Mapper} chain or <em>null</em> for the default chain
     * @param converterLookup the instance that is used to lookup the converters
     * @param converterRegistry an instance to manage the converter instances or <em>null</em> to prevent any further
     *            registry (including annotations)
     * @throws InitializationException in case of an initialization problem
     * @since 1.4.5
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
            ClassLoaderReference classLoaderReference, Mapper mapper, ConverterLookup converterLookup,
            ConverterRegistry converterRegistry) {
        if (reflectionProvider == null) {
            reflectionProvider = JVM.newReflectionProvider();
        }
        this.reflectionProvider = reflectionProvider;
        this.hierarchicalStreamDriver = driver;
        this.classLoaderReference = classLoaderReference;
        this.converterLookup = converterLookup;
        this.converterRegistry = converterRegistry;
        this.mapper = mapper == null ? buildMapper() : mapper;

        setupMappers();
        setupSecurity();
        setupAliases();
        setupDefaultImplementations();
        setupConverters();
        setupImmutableTypes();
        setMode(XPATH_RELATIVE_REFERENCES);
    }

    private Mapper buildMapper() {
        Mapper mapper = new DefaultMapper(classLoaderReference);
        if (useXStream11XmlFriendlyMapper()) {
            mapper = new XStream11XmlFriendlyMapper(mapper);
        }
        mapper = new DynamicProxyMapper(mapper);
        mapper = new PackageAliasingMapper(mapper);
        mapper = new ClassAliasingMapper(mapper);
        mapper = new ElementIgnoringMapper(mapper);
        mapper = new FieldAliasingMapper(mapper);
        mapper = new AttributeAliasingMapper(mapper);
        mapper = new SystemAttributeAliasingMapper(mapper);
        mapper = new ImplicitCollectionMapper(mapper, reflectionProvider);
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        mapper = new AttributeMapper(mapper, converterLookup, reflectionProvider);
        if (JVM.isVersion(5)) {
            mapper = buildMapperDynamically("com.thoughtworks.xstream.mapper.EnumMapper", new Class[]{Mapper.class},
                new Object[]{mapper});
        }
        mapper = new LocalConversionMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        if (JVM.isVersion(8)) {
            mapper = buildMapperDynamically("com.thoughtworks.xstream.mapper.LambdaMapper", new Class[]{Mapper.class},
                new Object[]{mapper});
        }
        mapper = new SecurityMapper(mapper);
        if (JVM.isVersion(5)) {
            mapper = buildMapperDynamically(ANNOTATION_MAPPER_TYPE, new Class[]{
                Mapper.class, ConverterRegistry.class, ConverterLookup.class, ClassLoaderReference.class,
                ReflectionProvider.class}, new Object[]{
                    mapper, converterRegistry, converterLookup, classLoaderReference, reflectionProvider});
        }
        mapper = wrapMapper((MapperWrapper)mapper);
        mapper = new CachingMapper(mapper);
        return mapper;
    }

    private Mapper buildMapperDynamically(String className, Class[] constructorParamTypes,
            Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, classLoaderReference.getReference());
            Constructor constructor = type.getConstructor(constructorParamTypes);
            return (Mapper)constructor.newInstance(constructorParamValues);
        } catch (Exception e) {
            throw new com.thoughtworks.xstream.InitializationException("Could not instantiate mapper : " + className,
                e);
        } catch (LinkageError e) {
            throw new com.thoughtworks.xstream.InitializationException("Could not instantiate mapper : " + className,
                e);
        }
    }

    protected MapperWrapper wrapMapper(MapperWrapper next) {
        return next;
    }

    /**
     * @deprecated As of 1.4.8
     */
    protected boolean useXStream11XmlFriendlyMapper() {
        return false;
    }

    private void setupMappers() {
        packageAliasingMapper = (PackageAliasingMapper)this.mapper.lookupMapperOfType(PackageAliasingMapper.class);
        classAliasingMapper = (ClassAliasingMapper)this.mapper.lookupMapperOfType(ClassAliasingMapper.class);
        elementIgnoringMapper = (ElementIgnoringMapper)this.mapper.lookupMapperOfType(ElementIgnoringMapper.class);
        fieldAliasingMapper = (FieldAliasingMapper)this.mapper.lookupMapperOfType(FieldAliasingMapper.class);
        attributeMapper = (AttributeMapper)this.mapper.lookupMapperOfType(AttributeMapper.class);
        attributeAliasingMapper = (AttributeAliasingMapper)this.mapper
            .lookupMapperOfType(AttributeAliasingMapper.class);
        systemAttributeAliasingMapper = (SystemAttributeAliasingMapper)this.mapper
            .lookupMapperOfType(SystemAttributeAliasingMapper.class);
        implicitCollectionMapper = (ImplicitCollectionMapper)this.mapper
            .lookupMapperOfType(ImplicitCollectionMapper.class);
        defaultImplementationsMapper = (DefaultImplementationsMapper)this.mapper
            .lookupMapperOfType(DefaultImplementationsMapper.class);
        immutableTypesMapper = (ImmutableTypesMapper)this.mapper.lookupMapperOfType(ImmutableTypesMapper.class);
        localConversionMapper = (LocalConversionMapper)this.mapper.lookupMapperOfType(LocalConversionMapper.class);
        securityMapper = (SecurityMapper)this.mapper.lookupMapperOfType(SecurityMapper.class);
        annotationConfiguration = (AnnotationConfiguration)this.mapper
            .lookupMapperOfType(AnnotationConfiguration.class);
    }

    protected void setupSecurity() {
        if (securityMapper == null) {
            return;
        }

        addPermission(AnyTypePermission.ANY);
        denyTypes(new String[]{
            "java.beans.EventHandler", //
            "java.lang.ProcessBuilder", //
            "javax.imageio.ImageIO$ContainsFilter", //
            "jdk.nashorn.internal.objects.NativeString"});
        denyTypesByRegExp(new Pattern[]{LAZY_ITERATORS, JAVAX_CRYPTO});
        allowTypeHierarchy(Exception.class);
        securityInitialized = false;
    }

    /**
     * Setup the security framework of a XStream instance.
     * <p>
     * This method is a pure helper method for XStream 1.4.x. It initializes an XStream instance with a white list of
     * well-known and simply types of the Java runtime as it is done in XStream 1.5.x by default. This method will do
     * therefore nothing in XStream 1.5.
     * </p>
     * 
     * @param xstream
     * @since 1.4.10
     */
    public static void setupDefaultSecurity(final XStream xstream) {
        if (!xstream.securityInitialized) {
            xstream.addPermission(NoTypePermission.NONE);
            xstream.addPermission(NullPermission.NULL);
            xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
            xstream.addPermission(ArrayTypePermission.ARRAYS);
            xstream.addPermission(InterfaceTypePermission.INTERFACES);
            xstream.allowTypeHierarchy(Calendar.class);
            xstream.allowTypeHierarchy(Collection.class);
            xstream.allowTypeHierarchy(Map.class);
            xstream.allowTypeHierarchy(Map.Entry.class);
            xstream.allowTypeHierarchy(Member.class);
            xstream.allowTypeHierarchy(Number.class);
            xstream.allowTypeHierarchy(Throwable.class);
            xstream.allowTypeHierarchy(TimeZone.class);

            Class type = JVM.loadClassForName("java.lang.Enum");
            if (type != null) {
                xstream.allowTypeHierarchy(type);
            }
            type = JVM.loadClassForName("java.nio.file.Path");
            if (type != null) {
                xstream.allowTypeHierarchy(type);
            }

            final Set types = new HashSet();
            types.add(BitSet.class);
            types.add(Charset.class);
            types.add(Class.class);
            types.add(Currency.class);
            types.add(Date.class);
            types.add(DecimalFormatSymbols.class);
            types.add(File.class);
            types.add(Locale.class);
            types.add(Object.class);
            types.add(Pattern.class);
            types.add(StackTraceElement.class);
            types.add(String.class);
            types.add(StringBuffer.class);
            types.add(JVM.loadClassForName("java.lang.StringBuilder"));
            types.add(URL.class);
            types.add(URI.class);
            types.add(JVM.loadClassForName("java.util.UUID"));
            if (JVM.isSQLAvailable()) {
                types.add(JVM.loadClassForName("java.sql.Timestamp"));
                types.add(JVM.loadClassForName("java.sql.Time"));
                types.add(JVM.loadClassForName("java.sql.Date"));
            }
            if (JVM.isVersion(8)) {
                xstream.allowTypeHierarchy(JVM.loadClassForName("java.time.Clock"));
                types.add(JVM.loadClassForName("java.time.Duration"));
                types.add(JVM.loadClassForName("java.time.Instant"));
                types.add(JVM.loadClassForName("java.time.LocalDate"));
                types.add(JVM.loadClassForName("java.time.LocalDateTime"));
                types.add(JVM.loadClassForName("java.time.LocalTime"));
                types.add(JVM.loadClassForName("java.time.MonthDay"));
                types.add(JVM.loadClassForName("java.time.OffsetDateTime"));
                types.add(JVM.loadClassForName("java.time.OffsetTime"));
                types.add(JVM.loadClassForName("java.time.Period"));
                types.add(JVM.loadClassForName("java.time.Ser"));
                types.add(JVM.loadClassForName("java.time.Year"));
                types.add(JVM.loadClassForName("java.time.YearMonth"));
                types.add(JVM.loadClassForName("java.time.ZonedDateTime"));
                xstream.allowTypeHierarchy(JVM.loadClassForName("java.time.ZoneId"));
                types.add(JVM.loadClassForName("java.time.chrono.HijrahDate"));
                types.add(JVM.loadClassForName("java.time.chrono.JapaneseDate"));
                types.add(JVM.loadClassForName("java.time.chrono.JapaneseEra"));
                types.add(JVM.loadClassForName("java.time.chrono.MinguoDate"));
                types.add(JVM.loadClassForName("java.time.chrono.ThaiBuddhistDate"));
                types.add(JVM.loadClassForName("java.time.chrono.Ser"));
                xstream.allowTypeHierarchy(JVM.loadClassForName("java.time.chrono.Chronology"));
                types.add(JVM.loadClassForName("java.time.temporal.ValueRange"));
                types.add(JVM.loadClassForName("java.time.temporal.WeekFields"));
            }
            types.remove(null);

            final Iterator iter = types.iterator();
            final Class[] classes = new Class[types.size()];
            for (int i = 0; i < classes.length; ++i) {
                classes[i] = (Class)iter.next();
            }
            xstream.allowTypes(classes);
        } else {
            throw new IllegalArgumentException("Security framework of XStream instance already initialized");
        }
    }

    protected void setupAliases() {
        if (classAliasingMapper == null) {
            return;
        }

        alias("null", Mapper.Null.class);
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
        alias("big-int", BigInteger.class);
        alias("big-decimal", BigDecimal.class);

        alias("string-buffer", StringBuffer.class);
        alias("string", String.class);
        alias("java-class", Class.class);
        alias("method", Method.class);
        alias("constructor", Constructor.class);
        alias("field", Field.class);
        alias("date", Date.class);
        alias("uri", URI.class);
        alias("url", URL.class);
        alias("bit-set", BitSet.class);

        alias("map", Map.class);
        alias("entry", Map.Entry.class);
        alias("properties", Properties.class);
        alias("list", List.class);
        alias("set", Set.class);
        alias("sorted-set", SortedSet.class);

        alias("linked-list", LinkedList.class);
        alias("vector", Vector.class);
        alias("tree-map", TreeMap.class);
        alias("tree-set", TreeSet.class);
        alias("hashtable", Hashtable.class);

        alias("empty-list", Collections.EMPTY_LIST.getClass());
        alias("empty-map", Collections.EMPTY_MAP.getClass());
        alias("empty-set", Collections.EMPTY_SET.getClass());
        alias("singleton-list", Collections.singletonList(this).getClass());
        alias("singleton-map", Collections.singletonMap(this, null).getClass());
        alias("singleton-set", Collections.singleton(this).getClass());

        if (JVM.isAWTAvailable()) {
            // Instantiating these two classes starts the AWT system, which is undesirable.
            // Calling loadClass ensures a reference to the class is found but they are not
            // instantiated.
            alias("awt-color", JVM.loadClassForName("java.awt.Color", false));
            alias("awt-font", JVM.loadClassForName("java.awt.Font", false));
            alias("awt-text-attribute", JVM.loadClassForName("java.awt.font.TextAttribute"));
        }

        Class type = JVM.loadClassForName("javax.activation.ActivationDataFlavor");
        if (type != null) {
            alias("activation-data-flavor", type);
        }

        if (JVM.isSQLAvailable()) {
            alias("sql-timestamp", JVM.loadClassForName("java.sql.Timestamp"));
            alias("sql-time", JVM.loadClassForName("java.sql.Time"));
            alias("sql-date", JVM.loadClassForName("java.sql.Date"));
        }

        alias("file", File.class);
        alias("locale", Locale.class);
        alias("gregorian-calendar", Calendar.class);

        if (JVM.isVersion(4)) {
            aliasDynamically("auth-subject", "javax.security.auth.Subject");
            alias("linked-hash-map", JVM.loadClassForName("java.util.LinkedHashMap"));
            alias("linked-hash-set", JVM.loadClassForName("java.util.LinkedHashSet"));
            alias("trace", JVM.loadClassForName("java.lang.StackTraceElement"));
            alias("currency", JVM.loadClassForName("java.util.Currency"));
            aliasType("charset", JVM.loadClassForName("java.nio.charset.Charset"));
        }

        if (JVM.isVersion(5)) {
            aliasDynamically("xml-duration", "javax.xml.datatype.Duration");
            alias("concurrent-hash-map", JVM.loadClassForName("java.util.concurrent.ConcurrentHashMap"));
            alias("enum-set", JVM.loadClassForName("java.util.EnumSet"));
            alias("enum-map", JVM.loadClassForName("java.util.EnumMap"));
            alias("string-builder", JVM.loadClassForName("java.lang.StringBuilder"));
            alias("uuid", JVM.loadClassForName("java.util.UUID"));
        }

        if (JVM.isVersion(7)) {
            aliasType("path", JVM.loadClassForName("java.nio.file.Path"));
        }

        if (JVM.isVersion(8)) {
            alias("fixed-clock", JVM.loadClassForName("java.time.Clock$FixedClock"));
            alias("offset-clock", JVM.loadClassForName("java.time.Clock$OffsetClock"));
            alias("system-clock", JVM.loadClassForName("java.time.Clock$SystemClock"));
            alias("tick-clock", JVM.loadClassForName("java.time.Clock$TickClock"));
            alias("day-of-week", JVM.loadClassForName("java.time.DayOfWeek"));
            alias("duration", JVM.loadClassForName("java.time.Duration"));
            alias("instant", JVM.loadClassForName("java.time.Instant"));
            alias("local-date", JVM.loadClassForName("java.time.LocalDate"));
            alias("local-date-time", JVM.loadClassForName("java.time.LocalDateTime"));
            alias("local-time", JVM.loadClassForName("java.time.LocalTime"));
            alias("month", JVM.loadClassForName("java.time.Month"));
            alias("month-day", JVM.loadClassForName("java.time.MonthDay"));
            alias("offset-date-time", JVM.loadClassForName("java.time.OffsetDateTime"));
            alias("offset-time", JVM.loadClassForName("java.time.OffsetTime"));
            alias("period", JVM.loadClassForName("java.time.Period"));
            alias("year", JVM.loadClassForName("java.time.Year"));
            alias("year-month", JVM.loadClassForName("java.time.YearMonth"));
            alias("zoned-date-time", JVM.loadClassForName("java.time.ZonedDateTime"));
            aliasType("zone-id", JVM.loadClassForName("java.time.ZoneId"));
            aliasType("chronology", JVM.loadClassForName("java.time.chrono.Chronology"));
            alias("hijrah-date", JVM.loadClassForName("java.time.chrono.HijrahDate"));
            alias("hijrah-era", JVM.loadClassForName("java.time.chrono.HijrahEra"));
            alias("japanese-date", JVM.loadClassForName("java.time.chrono.JapaneseDate"));
            alias("japanese-era", JVM.loadClassForName("java.time.chrono.JapaneseEra"));
            alias("minguo-date", JVM.loadClassForName("java.time.chrono.MinguoDate"));
            alias("minguo-era", JVM.loadClassForName("java.time.chrono.MinguoEra"));
            alias("thai-buddhist-date", JVM.loadClassForName("java.time.chrono.ThaiBuddhistDate"));
            alias("thai-buddhist-era", JVM.loadClassForName("java.time.chrono.ThaiBuddhistEra"));
            alias("chrono-field", JVM.loadClassForName("java.time.temporal.ChronoField"));
            alias("chrono-unit", JVM.loadClassForName("java.time.temporal.ChronoUnit"));
            alias("iso-field", JVM.loadClassForName("java.time.temporal.IsoFields$Field"));
            alias("iso-unit", JVM.loadClassForName("java.time.temporal.IsoFields$Unit"));
            alias("julian-field", JVM.loadClassForName("java.time.temporal.JulianFields$Field"));
            alias("temporal-value-range", JVM.loadClassForName("java.time.temporal.ValueRange"));
            alias("week-fields", JVM.loadClassForName("java.time.temporal.WeekFields"));
        }

        if (JVM.loadClassForName("java.lang.invoke.SerializedLambda") != null) {
            aliasDynamically("serialized-lambda", "java.lang.invoke.SerializedLambda");
        }
    }

    private void aliasDynamically(String alias, String className) {
        Class type = JVM.loadClassForName(className);
        if (type != null) {
            alias(alias, type);
        }
    }

    protected void setupDefaultImplementations() {
        if (defaultImplementationsMapper == null) {
            return;
        }
        addDefaultImplementation(HashMap.class, Map.class);
        addDefaultImplementation(ArrayList.class, List.class);
        addDefaultImplementation(HashSet.class, Set.class);
        addDefaultImplementation(TreeSet.class, SortedSet.class);
        addDefaultImplementation(GregorianCalendar.class, Calendar.class);
    }

    protected void setupConverters() {
        registerConverter(new ReflectionConverter(mapper, reflectionProvider), PRIORITY_VERY_LOW);

        registerConverter(new SerializableConverter(mapper, reflectionProvider, classLoaderReference), PRIORITY_LOW);
        registerConverter(new ExternalizableConverter(mapper, classLoaderReference), PRIORITY_LOW);

        registerConverter(new NullConverter(), PRIORITY_VERY_HIGH);
        registerConverter(new IntConverter(), PRIORITY_NORMAL);
        registerConverter(new FloatConverter(), PRIORITY_NORMAL);
        registerConverter(new DoubleConverter(), PRIORITY_NORMAL);
        registerConverter(new LongConverter(), PRIORITY_NORMAL);
        registerConverter(new ShortConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new CharConverter(), PRIORITY_NORMAL);
        registerConverter(new BooleanConverter(), PRIORITY_NORMAL);
        registerConverter(new ByteConverter(), PRIORITY_NORMAL);

        registerConverter(new StringConverter(), PRIORITY_NORMAL);
        registerConverter(new StringBufferConverter(), PRIORITY_NORMAL);
        registerConverter(new DateConverter(), PRIORITY_NORMAL);
        registerConverter(new BitSetConverter(), PRIORITY_NORMAL);
        registerConverter(new URIConverter(), PRIORITY_NORMAL);
        registerConverter(new URLConverter(), PRIORITY_NORMAL);
        registerConverter(new BigIntegerConverter(), PRIORITY_NORMAL);
        registerConverter(new BigDecimalConverter(), PRIORITY_NORMAL);

        registerConverter(new ArrayConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new CharArrayConverter(), PRIORITY_NORMAL);
        registerConverter(new CollectionConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new MapConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new TreeMapConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new TreeSetConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new SingletonCollectionConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new SingletonMapConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new PropertiesConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new EncodedByteArrayConverter(), PRIORITY_NORMAL);

        registerConverter(new FileConverter(), PRIORITY_NORMAL);
        if (JVM.isSQLAvailable()) {
            registerConverter(new SqlTimestampConverter(), PRIORITY_NORMAL);
            registerConverter(new SqlTimeConverter(), PRIORITY_NORMAL);
            registerConverter(new SqlDateConverter(), PRIORITY_NORMAL);
        }
        registerConverter(new DynamicProxyConverter(mapper, classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaClassConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaMethodConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaFieldConverter(classLoaderReference), PRIORITY_NORMAL);

        if (JVM.isAWTAvailable()) {
            registerConverter(new FontConverter(mapper), PRIORITY_NORMAL);
            registerConverter(new ColorConverter(), PRIORITY_NORMAL);
            registerConverter(new TextAttributeConverter(), PRIORITY_NORMAL);
        }
        if (JVM.isSwingAvailable()) {
            registerConverter(new LookAndFeelConverter(mapper, reflectionProvider), PRIORITY_NORMAL);
        }
        registerConverter(new LocaleConverter(), PRIORITY_NORMAL);
        registerConverter(new GregorianCalendarConverter(), PRIORITY_NORMAL);

        if (JVM.isVersion(4)) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.SubjectConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.ThrowableConverter",
                PRIORITY_NORMAL, new Class[]{ConverterLookup.class}, new Object[]{converterLookup});
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.StackTraceElementConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.CurrencyConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.RegexPatternConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.CharsetConverter",
                PRIORITY_NORMAL, null, null);
        }

        if (JVM.isVersion(5)) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            if (JVM.loadClassForName("javax.xml.datatype.Duration") != null) {
                registerConverterDynamically("com.thoughtworks.xstream.converters.extended.DurationConverter",
                    PRIORITY_NORMAL, null, null);
            }
            registerConverterDynamically("com.thoughtworks.xstream.converters.enums.EnumConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.enums.EnumSetConverter", PRIORITY_NORMAL,
                new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.enums.EnumMapConverter", PRIORITY_NORMAL,
                new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.basic.StringBuilderConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.basic.UUIDConverter", PRIORITY_NORMAL,
                null, null);
        }
        if (JVM.loadClassForName("javax.activation.ActivationDataFlavor") != null) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.ActivationDataFlavorConverter",
                PRIORITY_NORMAL, null, null);
        }
        if (JVM.isVersion(7)) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.PathConverter", PRIORITY_NORMAL,
                null, null);
        }
        if (JVM.isVersion(8)) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.ChronologyConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.DurationConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.HijrahDateConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.JapaneseDateConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.JapaneseEraConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.InstantConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.LocalDateConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.LocalDateTimeConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.LocalTimeConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.MinguoDateConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.MonthDayConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.OffsetDateTimeConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.OffsetTimeConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.PeriodConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.SystemClockConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.ThaiBuddhistDateConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.ValueRangeConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.WeekFieldsConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.YearConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.YearMonthConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.ZonedDateTimeConverter",
                PRIORITY_NORMAL, null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.time.ZoneIdConverter", PRIORITY_NORMAL,
                null, null);
            registerConverterDynamically("com.thoughtworks.xstream.converters.reflection.LambdaConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class, ReflectionProvider.class, ClassLoaderReference.class},
                new Object[]{mapper, reflectionProvider, classLoaderReference});
        }

        registerConverter(new SelfStreamingInstanceChecker(converterLookup, this), PRIORITY_NORMAL);
    }

    private void registerConverterDynamically(String className, int priority, Class[] constructorParamTypes,
            Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, classLoaderReference.getReference());
            Constructor constructor = type.getConstructor(constructorParamTypes);
            Object instance = constructor.newInstance(constructorParamValues);
            if (instance instanceof Converter) {
                registerConverter((Converter)instance, priority);
            } else if (instance instanceof SingleValueConverter) {
                registerConverter((SingleValueConverter)instance, priority);
            }
        } catch (Exception e) {
            throw new com.thoughtworks.xstream.InitializationException("Could not instantiate converter : " + className,
                e);
        } catch (LinkageError e) {
            throw new com.thoughtworks.xstream.InitializationException("Could not instantiate converter : " + className,
                e);
        }
    }

    protected void setupImmutableTypes() {
        if (immutableTypesMapper == null) {
            return;
        }

        // primitives are always immutable
        addImmutableType(boolean.class, false);
        addImmutableType(Boolean.class, false);
        addImmutableType(byte.class, false);
        addImmutableType(Byte.class, false);
        addImmutableType(char.class, false);
        addImmutableType(Character.class, false);
        addImmutableType(double.class, false);
        addImmutableType(Double.class, false);
        addImmutableType(float.class, false);
        addImmutableType(Float.class, false);
        addImmutableType(int.class, false);
        addImmutableType(Integer.class, false);
        addImmutableType(long.class, false);
        addImmutableType(Long.class, false);
        addImmutableType(short.class, false);
        addImmutableType(Short.class, false);

        // additional types
        addImmutableType(Mapper.Null.class, false);
        addImmutableType(BigDecimal.class, false);
        addImmutableType(BigInteger.class, false);
        addImmutableType(String.class, false);
        addImmutableType(URL.class, false);
        addImmutableType(File.class, false);
        addImmutableType(Class.class, false);

        if (JVM.isVersion(7)) {
            Class type = JVM.loadClassForName("java.nio.file.Paths");
            if (type != null) {
                Method methodGet;
                try {
                    methodGet = type.getDeclaredMethod("get", new Class[]{String.class, String[].class});
                    if (methodGet != null) {
                        Object path = methodGet.invoke(null, new Object[]{".", new String[0]});
                        if (path != null) {
                            addImmutableType(path.getClass(), false);
                        }
                    }
                } catch (NoSuchMethodException e) {
                } catch (SecurityException e) {
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }

        if (JVM.isAWTAvailable()) {
            addImmutableTypeDynamically("java.awt.font.TextAttribute", false);
        }

        if (JVM.isVersion(4)) {
            // late bound types - allows XStream to be compiled on earlier JDKs
            addImmutableTypeDynamically("java.nio.charset.Charset", true);
            addImmutableTypeDynamically("java.util.Currency", true);
        }

        if (JVM.isVersion(5)) {
            addImmutableTypeDynamically("java.util.UUID", true);
        }

        addImmutableType(URI.class, true);
        addImmutableType(Collections.EMPTY_LIST.getClass(), true);
        addImmutableType(Collections.EMPTY_SET.getClass(), true);
        addImmutableType(Collections.EMPTY_MAP.getClass(), true);

        if (JVM.isVersion(8)) {
            addImmutableTypeDynamically("java.time.Duration", false);
            addImmutableTypeDynamically("java.time.Instant", false);
            addImmutableTypeDynamically("java.time.LocalDate", false);
            addImmutableTypeDynamically("java.time.LocalDateTime", false);
            addImmutableTypeDynamically("java.time.LocalTime", false);
            addImmutableTypeDynamically("java.time.MonthDay", false);
            addImmutableTypeDynamically("java.time.OffsetDateTime", false);
            addImmutableTypeDynamically("java.time.OffsetTime", false);
            addImmutableTypeDynamically("java.time.Period", false);
            addImmutableTypeDynamically("java.time.Year", false);
            addImmutableTypeDynamically("java.time.YearMonth", false);
            addImmutableTypeDynamically("java.time.ZonedDateTime", false);
            addImmutableTypeDynamically("java.time.ZoneId", false);
            addImmutableTypeDynamically("java.time.ZoneOffset", false);
            addImmutableTypeDynamically("java.time.ZoneRegion", false);
            addImmutableTypeDynamically("java.time.chrono.HijrahChronology", false);
            addImmutableTypeDynamically("java.time.chrono.HijrahDate", false);
            addImmutableTypeDynamically("java.time.chrono.IsoChronology", false);
            addImmutableTypeDynamically("java.time.chrono.JapaneseChronology", false);
            addImmutableTypeDynamically("java.time.chrono.JapaneseDate", false);
            addImmutableTypeDynamically("java.time.chrono.JapaneseEra", false);
            addImmutableTypeDynamically("java.time.chrono.MinguoChronology", false);
            addImmutableTypeDynamically("java.time.chrono.MinguoDate", false);
            addImmutableTypeDynamically("java.time.chrono.ThaiBuddhistChronology", false);
            addImmutableTypeDynamically("java.time.chrono.ThaiBuddhistDate", false);
            addImmutableTypeDynamically("java.time.temporal.IsoFields$Field", false);
            addImmutableTypeDynamically("java.time.temporal.IsoFields$Unit", false);
            addImmutableTypeDynamically("java.time.temporal.JulianFields$Field", false);
        }
    }

    private void addImmutableTypeDynamically(String className, boolean isReferenceable) {
        Class type = JVM.loadClassForName(className);
        if (type != null) {
            addImmutableType(type, isReferenceable);
        }
    }

    public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    /**
     * Serialize an object to a pretty-printed XML String.
     *
     * @throws XStreamException if the object cannot be serialized
     */
    public String toXML(Object obj) {
        Writer writer = new StringWriter();
        toXML(obj, writer);
        return writer.toString();
    }

    /**
     * Serialize an object to the given Writer as pretty-printed XML. The Writer will be flushed afterwards and in case
     * of an exception.
     * 
     * @throws XStreamException if the object cannot be serialized
     */
    public void toXML(Object obj, Writer out) {
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
        try {
            marshal(obj, writer);
        } finally {
            writer.flush();
        }
    }

    /**
     * Serialize an object to the given OutputStream as pretty-printed XML. The OutputStream will be flushed afterwards
     * and in case of an exception.
     * 
     * @throws XStreamException if the object cannot be serialized
     */
    public void toXML(Object obj, OutputStream out) {
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
        try {
            marshal(obj, writer);
        } finally {
            writer.flush();
        }
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     *
     * @throws XStreamException if the object cannot be serialized
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer) {
        marshal(obj, writer, null);
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     * 
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If not present,
     *            XStream shall create one lazily as needed.
     * @throws XStreamException if the object cannot be serialized
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, DataHolder dataHolder) {
        marshallingStrategy.marshal(writer, obj, converterLookup, mapper, dataHolder);
    }

    /**
     * Deserialize an object from an XML String.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(String xml) {
        return fromXML(new StringReader(xml));
    }

    /**
     * Deserialize an object from an XML Reader.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(Reader reader) {
        return unmarshal(hierarchicalStreamDriver.createReader(reader), null);
    }

    /**
     * Deserialize an object from an XML InputStream.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(InputStream input) {
        return unmarshal(hierarchicalStreamDriver.createReader(input), null);
    }

    /**
     * Deserialize an object from a URL. Depending on the parser implementation, some might take the file path as
     * SystemId to resolve additional references.
     * 
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public Object fromXML(URL url) {
        return fromXML(url, null);
    }

    /**
     * Deserialize an object from a file. Depending on the parser implementation, some might take the file path as
     * SystemId to resolve additional references.
     * 
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public Object fromXML(File file) {
        return fromXML(file, null);
    }

    /**
     * Deserialize an object from an XML String, populating the fields of the given root object instead of instantiating
     * a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into
     * the raw memory area of the existing object. Use with care!
     * 
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(String xml, Object root) {
        return fromXML(new StringReader(xml), root);
    }

    /**
     * Deserialize an object from an XML Reader, populating the fields of the given root object instead of instantiating
     * a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into
     * the raw memory area of the existing object. Use with care!
     * 
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(Reader xml, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), root);
    }

    /**
     * Deserialize an object from a URL, populating the fields of the given root object instead of instantiating a new
     * one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into the raw
     * memory area of the existing object. Use with care! Depending on the parser implementation, some might take the
     * file path as SystemId to resolve additional references.
     * 
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public Object fromXML(URL url, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(url), root);
    }

    /**
     * Deserialize an object from a file, populating the fields of the given root object instead of instantiating a new
     * one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into the raw
     * memory area of the existing object. Use with care! Depending on the parser implementation, some might take the
     * file path as SystemId to resolve additional references.
     * 
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public Object fromXML(File file, Object root) {
        HierarchicalStreamReader reader = hierarchicalStreamDriver.createReader(file);
        try {
            return unmarshal(reader, root);
        } finally {
            reader.close();
        }
    }

    /**
     * Deserialize an object from an XML InputStream, populating the fields of the given root object instead of
     * instantiating a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write
     * directly into the raw memory area of the existing object. Use with care!
     * 
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object fromXML(InputStream input, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(input), root);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader) {
        return unmarshal(reader, null, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML), populating the fields of the given root
     * object instead of instantiating a new one. Note, that this is a special use case! With the ReflectionConverter
     * XStream will write directly into the raw memory area of the existing object. Use with care!
     * 
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root) {
        return unmarshal(reader, root, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     * 
     * @param root If present, the passed in object will have its fields populated, as opposed to XStream creating a new
     *            instance. Note, that this is a special use case! With the ReflectionConverter XStream will write
     *            directly into the raw memory area of the existing object. Use with care!
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If not present,
     *            XStream shall create one lazily as needed.
     * @throws XStreamException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root, DataHolder dataHolder) {
        try {
            if (!securityInitialized && !securityWarningGiven) {
                securityWarningGiven = true;
                System.err
                    .println(
                        "Security framework of XStream not explicitly initialized, using predefined black list on your own risk.");
            }
            return marshallingStrategy.unmarshal(root, reader, dataHolder, converterLookup, mapper);

        } catch (ConversionException e) {
            Package pkg = getClass().getPackage();
            String version = pkg != null ? pkg.getImplementationVersion() : null;
            e.add("version", version != null ? version : "not available");
            throw e;
        }
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param name Short name
     * @param type Type to be aliased
     * @throws InitializationException if no {@link ClassAliasingMapper} is available
     */
    public void alias(String name, Class type) {
        if (classAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ClassAliasingMapper.class.getName()
                + " available");
        }
        classAliasingMapper.addClassAlias(name, type);
    }

    /**
     * Alias a type to a shorter name to be used in XML elements. Any class that is assignable to this type will be
     * aliased to the same name.
     * 
     * @param name Short name
     * @param type Type to be aliased
     * @since 1.2
     * @throws InitializationException if no {@link ClassAliasingMapper} is available
     */
    public void aliasType(String name, Class type) {
        if (classAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ClassAliasingMapper.class.getName()
                + " available");
        }
        classAliasingMapper.addTypeAlias(name, type);
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param name Short name
     * @param type Type to be aliased
     * @param defaultImplementation Default implementation of type to use if no other specified.
     * @throws InitializationException if no {@link DefaultImplementationsMapper} or no {@link ClassAliasingMapper} is
     *             available
     */
    public void alias(String name, Class type, Class defaultImplementation) {
        alias(name, type);
        addDefaultImplementation(defaultImplementation, type);
    }

    /**
     * Alias a package to a shorter name to be used in XML elements.
     *
     * @param name Short name
     * @param pkgName package to be aliased
     * @throws InitializationException if no {@link DefaultImplementationsMapper} or no {@link PackageAliasingMapper} is
     *             available
     * @since 1.3.1
     */
    public void aliasPackage(String name, String pkgName) {
        if (packageAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + PackageAliasingMapper.class.getName()
                + " available");
        }
        packageAliasingMapper.addPackageAlias(name, pkgName);
    }

    /**
     * Create an alias for a field name.
     *
     * @param alias the alias itself
     * @param definedIn the type that declares the field
     * @param fieldName the name of the field
     * @throws InitializationException if no {@link FieldAliasingMapper} is available
     */
    public void aliasField(String alias, Class definedIn, String fieldName) {
        if (fieldAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + FieldAliasingMapper.class.getName()
                + " available");
        }
        fieldAliasingMapper.addFieldAlias(alias, definedIn, fieldName);
    }

    /**
     * Create an alias for an attribute
     *
     * @param alias the alias itself
     * @param attributeName the name of the attribute
     * @throws InitializationException if no {@link AttributeAliasingMapper} is available
     */
    public void aliasAttribute(String alias, String attributeName) {
        if (attributeAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + AttributeAliasingMapper.class.getName()
                + " available");
        }
        attributeAliasingMapper.addAliasFor(attributeName, alias);
    }

    /**
     * Create an alias for a system attribute. XStream will not write a system attribute if its alias is set to
     * <code>null</code>. However, this is not reversible, i.e. deserialization of the result is likely to fail
     * afterwards and will not produce an object equal to the originally written one.
     * 
     * @param alias the alias itself (may be <code>null</code>)
     * @param systemAttributeName the name of the system attribute
     * @throws InitializationException if no {@link SystemAttributeAliasingMapper} is available
     * @since 1.3.1
     */
    public void aliasSystemAttribute(String alias, String systemAttributeName) {
        if (systemAttributeAliasingMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + SystemAttributeAliasingMapper.class.getName()
                + " available");
        }
        systemAttributeAliasingMapper.addAliasFor(systemAttributeName, alias);
    }

    /**
     * Create an alias for an attribute.
     *
     * @param definedIn the type where the attribute is defined
     * @param attributeName the name of the attribute
     * @param alias the alias itself
     * @throws InitializationException if no {@link AttributeAliasingMapper} is available
     * @since 1.2.2
     */
    public void aliasAttribute(Class definedIn, String attributeName, String alias) {
        aliasField(alias, definedIn, attributeName);
        useAttributeFor(definedIn, attributeName);
    }

    /**
     * Use an attribute for a field or a specific type.
     *
     * @param fieldName the name of the field
     * @param type the Class of the type to be rendered as XML attribute
     * @throws InitializationException if no {@link AttributeMapper} is available
     * @since 1.2
     */
    public void useAttributeFor(String fieldName, Class type) {
        if (attributeMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + AttributeMapper.class.getName()
                + " available");
        }
        attributeMapper.addAttributeFor(fieldName, type);
    }

    /**
     * Use an attribute for a field declared in a specific type.
     *
     * @param fieldName the name of the field
     * @param definedIn the Class containing such field
     * @throws InitializationException if no {@link AttributeMapper} is available
     * @since 1.2.2
     */
    public void useAttributeFor(Class definedIn, String fieldName) {
        if (attributeMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + AttributeMapper.class.getName()
                + " available");
        }
        attributeMapper.addAttributeFor(definedIn, fieldName);
    }

    /**
     * Use an attribute for an arbitrary type.
     *
     * @param type the Class of the type to be rendered as XML attribute
     * @throws InitializationException if no {@link AttributeMapper} is available
     * @since 1.2
     */
    public void useAttributeFor(Class type) {
        if (attributeMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + AttributeMapper.class.getName()
                + " available");
        }
        attributeMapper.addAttributeFor(type);
    }

    /**
     * Associate a default implementation of a class with an object. Whenever XStream encounters an instance of this
     * type, it will use the default implementation instead. For example, java.util.ArrayList is the default
     * implementation of java.util.List.
     * 
     * @param defaultImplementation
     * @param ofType
     * @throws InitializationException if no {@link DefaultImplementationsMapper} is available
     */
    public void addDefaultImplementation(Class defaultImplementation, Class ofType) {
        if (defaultImplementationsMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + DefaultImplementationsMapper.class.getName()
                + " available");
        }
        defaultImplementationsMapper.addDefaultImplementation(defaultImplementation, ofType);
    }

    /**
     * Add immutable types. The value of the instances of these types will always be written into the stream even if
     * they appear multiple times. However, references are still supported at deserialization time.
     *
     * @throws InitializationException if no {@link ImmutableTypesMapper} is available
     * @deprecated As of 1.4.9 use {@link #addImmutableType(Class, boolean)}
     */
    public void addImmutableType(Class type) {
        addImmutableType(type, true);
    }

    /**
     * Add immutable types. The value of the instances of these types will always be written into the stream even if
     * they appear multiple times.
     * <p>
     * Note, while a reference-keeping marshaller will not write references for immutable types into the stream, a
     * reference-keeping unmarshaller can still support such references in the stream for compatibility reasons at the
     * expense of memory consumption. Therefore declare these types only as referenceable if your already persisted
     * streams do contain such references. Otherwise you may waste a lot of memory during deserialization.
     * </p>
     *
     * @param isReferenceable <code>true</code> if support at deserialization time is required for compatibility at the
     *            cost of a higher memory footprint, <code>false</code> otherwise
     * @throws InitializationException if no {@link ImmutableTypesMapper} is available
     * @since 1.4.9
     */
    public void addImmutableType(final Class type, final boolean isReferenceable) {
        if (immutableTypesMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ImmutableTypesMapper.class.getName()
                + " available");
        }
        immutableTypesMapper.addImmutableType(type, isReferenceable);
    }

    public void registerConverter(Converter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    public void registerConverter(Converter converter, int priority) {
        if (converterRegistry != null) {
            converterRegistry.registerConverter(converter, priority);
        }
    }

    public void registerConverter(SingleValueConverter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    public void registerConverter(SingleValueConverter converter, int priority) {
        if (converterRegistry != null) {
            converterRegistry.registerConverter(new SingleValueConverterWrapper(converter), priority);
        }
    }

    /**
     * Register a local {@link Converter} for a field.
     *
     * @param definedIn the class type the field is defined in
     * @param fieldName the field name
     * @param converter the converter to use
     * @since 1.3
     */
    public void registerLocalConverter(Class definedIn, String fieldName, Converter converter) {
        if (localConversionMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + LocalConversionMapper.class.getName()
                + " available");
        }
        localConversionMapper.registerLocalConverter(definedIn, fieldName, converter);
    }

    /**
     * Register a local {@link SingleValueConverter} for a field.
     *
     * @param definedIn the class type the field is defined in
     * @param fieldName the field name
     * @param converter the converter to use
     * @since 1.3
     */
    public void registerLocalConverter(Class definedIn, String fieldName, SingleValueConverter converter) {
        registerLocalConverter(definedIn, fieldName, (Converter)new SingleValueConverterWrapper(converter));
    }

    /**
     * Retrieve the {@link Mapper}. This is by default a chain of {@link MapperWrapper MapperWrappers}.
     * 
     * @return the mapper
     * @since 1.2
     */
    public Mapper getMapper() {
        return mapper;
    }

    /**
     * Retrieve the {@link ReflectionProvider} in use.
     *
     * @return the mapper
     * @since 1.2.1
     */
    public ReflectionProvider getReflectionProvider() {
        return reflectionProvider;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }

    /**
     * Change mode for dealing with duplicate references. Valid values are <code>XPATH_ABSOLUTE_REFERENCES</code>,
     * <code>XPATH_RELATIVE_REFERENCES</code>, <code>XStream.ID_REFERENCES</code> and
     * <code>XStream.NO_REFERENCES</code>.
     * 
     * @throws IllegalArgumentException if the mode is not one of the declared types
     * @see #XPATH_ABSOLUTE_REFERENCES
     * @see #XPATH_RELATIVE_REFERENCES
     * @see #ID_REFERENCES
     * @see #NO_REFERENCES
     */
    public void setMode(int mode) {
        switch (mode) {
        case NO_REFERENCES:
            setMarshallingStrategy(new TreeMarshallingStrategy());
            break;
        case ID_REFERENCES:
            setMarshallingStrategy(new ReferenceByIdMarshallingStrategy());
            break;
        case XPATH_RELATIVE_REFERENCES:
            setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy(
                ReferenceByXPathMarshallingStrategy.RELATIVE));
            break;
        case XPATH_ABSOLUTE_REFERENCES:
            setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy(
                ReferenceByXPathMarshallingStrategy.ABSOLUTE));
            break;
        case SINGLE_NODE_XPATH_RELATIVE_REFERENCES:
            setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy(ReferenceByXPathMarshallingStrategy.RELATIVE
                | ReferenceByXPathMarshallingStrategy.SINGLE_NODE));
            break;
        case SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES:
            setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy(ReferenceByXPathMarshallingStrategy.ABSOLUTE
                | ReferenceByXPathMarshallingStrategy.SINGLE_NODE));
            break;
        default:
            throw new IllegalArgumentException("Unknown mode : " + mode);
        }
    }

    /**
     * Adds a default implicit collection which is used for any unmapped XML tag.
     *
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be a concrete collection type or matching
     *            the default implementation type of the collection type.
     */
    public void addImplicitCollection(Class ownerType, String fieldName) {
        addImplicitCollection(ownerType, fieldName, null, null);
    }

    /**
     * Adds implicit collection which is used for all items of the given itemType.
     *
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be a concrete collection type or matching
     *            the default implementation type of the collection type.
     * @param itemType type of the items to be part of this collection
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available
     */
    public void addImplicitCollection(Class ownerType, String fieldName, Class itemType) {
        addImplicitCollection(ownerType, fieldName, null, itemType);
    }

    /**
     * Adds implicit collection which is used for all items of the given element name defined by itemFieldName.
     * 
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be a concrete collection type or matching
     *            the default implementation type of the collection type.
     * @param itemFieldName element name of the implicit collection
     * @param itemType item type to be aliases be the itemFieldName
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available
     */
    public void addImplicitCollection(Class ownerType, String fieldName, String itemFieldName, Class itemType) {
        addImplicitMap(ownerType, fieldName, itemFieldName, itemType, null);
    }

    /**
     * Adds an implicit array.
     *
     * @param ownerType class owning the implicit array
     * @param fieldName name of the array field
     * @since 1.4
     */
    public void addImplicitArray(Class ownerType, String fieldName) {
        addImplicitCollection(ownerType, fieldName);
    }

    /**
     * Adds an implicit array which is used for all items of the given itemType when the array type matches.
     * 
     * @param ownerType class owning the implicit array
     * @param fieldName name of the array field in the ownerType
     * @param itemType type of the items to be part of this array
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available or the array type does not
     *             match the itemType
     * @since 1.4
     */
    public void addImplicitArray(Class ownerType, String fieldName, Class itemType) {
        addImplicitCollection(ownerType, fieldName, itemType);
    }

    /**
     * Adds an implicit array which is used for all items of the given element name defined by itemName.
     * 
     * @param ownerType class owning the implicit array
     * @param fieldName name of the array field in the ownerType
     * @param itemName alias name of the items
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available
     * @since 1.4
     */
    public void addImplicitArray(Class ownerType, String fieldName, String itemName) {
        addImplicitCollection(ownerType, fieldName, itemName, null);
    }

    /**
     * Adds an implicit map.
     *
     * @param ownerType class owning the implicit map
     * @param fieldName name of the field in the ownerType. This field must be a concrete map type or matching the
     *            default implementation type of the map type.
     * @param itemType type of the items to be part of this map as value
     * @param keyFieldName the name of the field of the itemType that is used for the key in the map
     * @since 1.4
     */
    public void addImplicitMap(Class ownerType, String fieldName, Class itemType, String keyFieldName) {
        addImplicitMap(ownerType, fieldName, null, itemType, keyFieldName);
    }

    /**
     * Adds an implicit map.
     *
     * @param ownerType class owning the implicit map
     * @param fieldName name of the field in the ownerType. This field must be a concrete map type or matching the
     *            default implementation type of the map type.
     * @param itemName alias name of the items
     * @param itemType type of the items to be part of this map as value
     * @param keyFieldName the name of the field of the itemType that is used for the key in the map
     * @since 1.4
     */
    public void addImplicitMap(Class ownerType, String fieldName, String itemName, Class itemType,
            String keyFieldName) {
        if (implicitCollectionMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ImplicitCollectionMapper.class.getName()
                + " available");
        }
        implicitCollectionMapper.add(ownerType, fieldName, itemName, itemType, keyFieldName);
    }

    /**
     * Create a DataHolder that can be used to pass data to the converters. The DataHolder is provided with a call to
     * {@link #marshal(Object, HierarchicalStreamWriter, DataHolder)},
     * {@link #unmarshal(HierarchicalStreamReader, Object, DataHolder)},
     * {@link #createObjectInputStream(HierarchicalStreamReader, DataHolder)} or
     * {@link #createObjectOutputStream(HierarchicalStreamWriter, String, DataHolder)}.
     *
     * @return a new {@link DataHolder}
     */
    public DataHolder newDataHolder() {
        return new MapBackedDataHolder();
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     * <p>
     * To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.
     * </p>
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(Writer writer) throws IOException {
        return createObjectOutputStream(hierarchicalStreamDriver.createWriter(writer), "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     * <p>
     * To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.
     * </p>
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(HierarchicalStreamWriter writer) throws IOException {
        return createObjectOutputStream(writer, "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(Writer writer, String rootNodeName) throws IOException {
        return createObjectOutputStream(hierarchicalStreamDriver.createWriter(writer), rootNodeName);
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the OutputStream using XStream.
     * <p>
     * To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.
     * </p>
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.3
     */
    public ObjectOutputStream createObjectOutputStream(OutputStream out) throws IOException {
        return createObjectOutputStream(hierarchicalStreamDriver.createWriter(out), "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the OutputStream using XStream.
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.3
     */
    public ObjectOutputStream createObjectOutputStream(OutputStream out, String rootNodeName) throws IOException {
        return createObjectOutputStream(hierarchicalStreamDriver.createWriter(out), rootNodeName);
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     * <p>
     * Because an ObjectOutputStream can contain multiple items and XML only allows a single root node, the stream must
     * be written inside an enclosing node.
     * </p>
     * <p>
     * It is necessary to call ObjectOutputStream.close() when done, otherwise the stream will be incomplete.
     * </p>
     * <h3>Example</h3>
     *
     * <pre>
     *  ObjectOutputStream out = xstream.createObjectOutputStream(aWriter, &quot;things&quot;);
     *   out.writeInt(123);
     *   out.writeObject(&quot;Hello&quot;);
     *   out.writeObject(someObject)
     *   out.close();
     * </pre>
     *
     * @param writer The writer to serialize the objects to.
     * @param rootNodeName The name of the root node enclosing the stream of objects.
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(final HierarchicalStreamWriter writer, final String rootNodeName)
            throws IOException {
        return createObjectOutputStream(writer, rootNodeName, null);
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.4.10
     */
    public ObjectOutputStream createObjectOutputStream(final HierarchicalStreamWriter writer, final String rootNodeName,
            final DataHolder dataHolder)
            throws IOException {
        final StatefulWriter statefulWriter = new StatefulWriter(writer);
        statefulWriter.startNode(rootNodeName, null);
        return new CustomObjectOutputStream(new CustomObjectOutputStream.StreamCallback() {
            public void writeToStream(final Object object) {
                marshal(object, statefulWriter, dataHolder);
            }

            public void writeFieldsToStream(Map fields) throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            public void defaultWriteObject() throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            public void flush() {
                statefulWriter.flush();
            }

            public void close() {
                if (statefulWriter.state() != StatefulWriter.STATE_CLOSED) {
                    statefulWriter.endNode();
                    statefulWriter.close();
                }
            }
        });
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using XStream.
     * 
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.0.3
     */
    public ObjectInputStream createObjectInputStream(Reader xmlReader) throws IOException {
        return createObjectInputStream(hierarchicalStreamDriver.createReader(xmlReader));
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from an InputStream using XStream.
     * 
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.3
     */
    public ObjectInputStream createObjectInputStream(InputStream in) throws IOException {
        return createObjectInputStream(hierarchicalStreamDriver.createReader(in));
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using XStream.
     * <p>
     * It is necessary to call ObjectInputStream.close() when done, otherwise the stream might keep system resources.
     * </p>
     * <h3>Example</h3>
     *
     * <pre>
     * ObjectInputStream in = xstream.createObjectOutputStream(aReader);
     * int a = out.readInt();
     * Object b = out.readObject();
     * Object c = out.readObject();
     * </pre>
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.0.3
     */
    public ObjectInputStream createObjectInputStream(final HierarchicalStreamReader reader) throws IOException {
        return createObjectInputStream(reader, null);
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using XStream.
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.4.10
     */
    public ObjectInputStream createObjectInputStream(final HierarchicalStreamReader reader, final DataHolder dataHolder)
            throws IOException {
        return new CustomObjectInputStream(new CustomObjectInputStream.StreamCallback() {
            public Object readFromStream() throws EOFException {
                if (!reader.hasMoreChildren()) {
                    throw new EOFException();
                }
                reader.moveDown();
                final Object result = unmarshal(reader, null, dataHolder);
                reader.moveUp();
                return result;
            }

            public Map readFieldsFromStream() throws IOException {
                throw new NotActiveException("not in call to readObject");
            }

            public void defaultReadObject() throws NotActiveException {
                throw new NotActiveException("not in call to readObject");
            }

            public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException {
                throw new NotActiveException("stream inactive");
            }

            public void close() {
                reader.close();
            }
        }, classLoaderReference);
    }

    /**
     * Change the ClassLoader XStream uses to load classes. Creating an XStream instance it will register for all kind
     * of classes and types of the current JDK, but not for any 3rd party type. To ensure that all other types are
     * loaded with your class loader, you should call this method as early as possible - or consider to provide the
     * class loader directly in the constructor.
     * 
     * @since 1.1.1
     */
    public void setClassLoader(ClassLoader classLoader) {
        classLoaderReference.setReference(classLoader);
    }

    /**
     * Retrieve the ClassLoader XStream uses to load classes.
     *
     * @since 1.1.1
     */
    public ClassLoader getClassLoader() {
        return classLoaderReference.getReference();
    }

    /**
     * Retrieve the reference to this instance' ClassLoader. Use this reference for other XStream components (like
     * converters) to ensure that they will use a changed ClassLoader instance automatically.
     * 
     * @return the reference
     * @since 1.4.5
     */
    public ClassLoaderReference getClassLoaderReference() {
        return classLoaderReference;
    }

    /**
     * Prevents a field from being serialized. To omit a field you must always provide the declaring type and not
     * necessarily the type that is converted.
     * 
     * @since 1.1.3
     * @throws InitializationException if no {@link ElementIgnoringMapper} is available
     */
    public void omitField(Class definedIn, String fieldName) {
        if (elementIgnoringMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ElementIgnoringMapper.class.getName()
                + " available");
        }
        elementIgnoringMapper.omitField(definedIn, fieldName);
    }

    /**
     * Ignore all unknown elements.
     *
     * @since 1.4.5
     */
    public void ignoreUnknownElements() {
        ignoreUnknownElements(IGNORE_ALL);
    }

    /**
     * Add pattern for unknown element names to ignore.
     *
     * @param pattern the name pattern as regular expression
     * @since 1.4.5
     */
    public void ignoreUnknownElements(String pattern) {
        ignoreUnknownElements(Pattern.compile(pattern));
    }

    /**
     * Add pattern for unknown element names to ignore.
     *
     * @param pattern the name pattern as regular expression
     * @since 1.4.5
     */
    public void ignoreUnknownElements(final Pattern pattern) {
        if (elementIgnoringMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ElementIgnoringMapper.class.getName()
                + " available");
        }
        elementIgnoringMapper.addElementsToIgnore(pattern);
    }

    /**
     * Process the annotations of the given types and configure the XStream.
     *
     * @param types the types with XStream annotations
     * @since 1.3
     */
    public void processAnnotations(final Class[] types) {
        if (annotationConfiguration == null) {
            throw new com.thoughtworks.xstream.InitializationException("No " + ANNOTATION_MAPPER_TYPE + " available");
        }
        annotationConfiguration.processAnnotations(types);
    }

    /**
     * Process the annotations of the given type and configure the XStream. A call of this method will automatically
     * turn the auto-detection mode for annotations off.
     * 
     * @param type the type with XStream annotations
     * @since 1.3
     */
    public void processAnnotations(final Class type) {
        processAnnotations(new Class[]{type});
    }

    /**
     * Set the auto-detection mode of the AnnotationMapper. Note that auto-detection implies that the XStream is
     * configured while it is processing the XML steams. This is a potential concurrency problem. Also is it technically
     * not possible to detect all class aliases at deserialization. You have been warned!
     * 
     * @param mode <code>true</code> if annotations are auto-detected
     * @since 1.3
     */
    public void autodetectAnnotations(boolean mode) {
        if (annotationConfiguration != null) {
            annotationConfiguration.autodetectAnnotations(mode);
        }
    }

    /**
     * Add a new security permission.
     * <p>
     * Permissions are evaluated in the added sequence. An instance of {@link NoTypePermission} or
     * {@link AnyTypePermission} will implicitly wipe any existing permission.
     * </p>
     *
     * @param permission the permission to add
     * @since 1.4.7
     */
    public void addPermission(TypePermission permission) {
        if (securityMapper != null) {
            securityInitialized |= permission.equals(NoTypePermission.NONE) || permission.equals(AnyTypePermission.ANY);
            securityMapper.addPermission(permission);
        }
    }

    /**
     * Add security permission for explicit types by name.
     *
     * @param names the type names to allow
     * @since 1.4.7
     */
    public void allowTypes(String[] names) {
        addPermission(new ExplicitTypePermission(names));
    }

    /**
     * Add security permission for explicit types.
     *
     * @param types the types to allow
     * @since 1.4.7
     */
    public void allowTypes(Class[] types) {
        addPermission(new ExplicitTypePermission(types));
    }

    /**
     * Add security permission for a type hierarchy.
     *
     * @param type the base type to allow
     * @since 1.4.7
     */
    public void allowTypeHierarchy(Class type) {
        addPermission(new TypeHierarchyPermission(type));
    }

    /**
     * Add security permission for types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to allow type names
     * @since 1.4.7
     */
    public void allowTypesByRegExp(String[] regexps) {
        addPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission for types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to allow type names
     * @since 1.4.7
     */
    public void allowTypesByRegExp(Pattern[] regexps) {
        addPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission for types matching one of the specified wildcard patterns.
     * <p>
     * Supported are patterns with path expressions using dot as separator:
     * </p>
     * <ul>
     * <li>?: one non-control character except separator, e.g. for 'java.net.Inet?Address'</li>
     * <li>*: arbitrary number of non-control characters except separator, e.g. for types in a package like
     * 'java.lang.*'</li>
     * <li>**: arbitrary number of non-control characters including separator, e.g. for types in a package and
     * subpackages like 'java.lang.**'</li>
     * </ul>
     *
     * @param patterns the patterns to allow type names
     * @since 1.4.7
     */
    public void allowTypesByWildcard(String[] patterns) {
        addPermission(new WildcardTypePermission(patterns));
    }

    /**
     * Add security permission denying another one.
     *
     * @param permission the permission to deny
     * @since 1.4.7
     */
    public void denyPermission(TypePermission permission) {
        addPermission(new NoPermission(permission));
    }

    /**
     * Add security permission forbidding explicit types by name.
     *
     * @param names the type names to forbid
     * @since 1.4.7
     */
    public void denyTypes(String[] names) {
        denyPermission(new ExplicitTypePermission(names));
    }

    /**
     * Add security permission forbidding explicit types.
     *
     * @param types the types to forbid
     * @since 1.4.7
     */
    public void denyTypes(Class[] types) {
        denyPermission(new ExplicitTypePermission(types));
    }

    /**
     * Add security permission forbidding a type hierarchy.
     *
     * @param type the base type to forbid
     * @since 1.4.7
     */
    public void denyTypeHierarchy(Class type) {
        denyPermission(new TypeHierarchyPermission(type));
    }

    /**
     * Add security permission forbidding types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to forbid type names
     * @since 1.4.7
     */
    public void denyTypesByRegExp(String[] regexps) {
        denyPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission forbidding types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to forbid type names
     * @since 1.4.7
     */
    public void denyTypesByRegExp(Pattern[] regexps) {
        denyPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission forbidding types matching one of the specified wildcard patterns.
     * <p>
     * Supported are patterns with path expressions using dot as separator:
     * </p>
     * <ul>
     * <li>?: one non-control character except separator, e.g. for 'java.net.Inet?Address'</li>
     * <li>*: arbitrary number of non-control characters except separator, e.g. for types in a package like
     * 'java.lang.*'</li>
     * <li>**: arbitrary number of non-control characters including separator, e.g. for types in a package and
     * subpackages like 'java.lang.**'</li>
     * </ul>
     *
     * @param patterns the patterns to forbid names
     * @since 1.4.7
     */
    public void denyTypesByWildcard(String[] patterns) {
        denyPermission(new WildcardTypePermission(patterns));
    }

    private Object readResolve() {
        securityWarningGiven = true;
        return this;
    }

    /**
     * @deprecated As of 1.3, use {@link com.thoughtworks.xstream.InitializationException} instead
     */
    public static class InitializationException extends XStreamException {
        /**
         * @deprecated As of 1.3, use
         *             {@link com.thoughtworks.xstream.InitializationException#InitializationException(String, Throwable)}
         *             instead
         */
        public InitializationException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @deprecated As of 1.3, use
         *             {@link com.thoughtworks.xstream.InitializationException#InitializationException(String)} instead
         */
        public InitializationException(String message) {
            super(message);
        }
    }
}
