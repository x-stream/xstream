package com.thoughtworks.xstream;

import java.awt.font.TextAttribute;
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

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
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
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import com.thoughtworks.xstream.converters.extended.FontConverter;
import com.thoughtworks.xstream.converters.extended.GregorianCalendarConverter;
import com.thoughtworks.xstream.converters.extended.JavaClassConverter;
import com.thoughtworks.xstream.converters.extended.JavaMethodConverter;
import com.thoughtworks.xstream.converters.extended.LocaleConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SelfStreamingInstanceChecker;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.core.BaseException;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.MapBackedDataHolder;
import com.thoughtworks.xstream.core.ReferenceByIdMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshallingStrategy;
import com.thoughtworks.xstream.core.util.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StatefulWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.mapper.XStream11XmlFriendlyMapper;


/**
 * Simple facade to XStream library, a Java-XML serialization tool. <p/>
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
 * <p/>
 * <h3>Aliasing classes</h3>
 * <p/>
 * <p>
 * To create shorter XML, you can specify aliases for classes using the <code>alias()</code>
 * method. For example, you can shorten all occurences of element
 * <code>&lt;com.blah.MyThing&gt;</code> to <code>&lt;my-thing&gt;</code> by registering an
 * alias for the class.
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
 * <p/>
 * <h3>Converters</h3>
 * <p/>
 * <p>
 * XStream contains a map of {@link com.thoughtworks.xstream.converters.Converter} instances, each
 * of which acts as a strategy for converting a particular type of class to XML and back again. Out
 * of the box, XStream contains converters for most basic types (String, Date, int, boolean, etc)
 * and collections (Map, List, Set, Properties, etc). For other objects reflection is used to
 * serialize each field recursively.
 * </p>
 * <p/>
 * <p>
 * Extra converters can be registered using the <code>registerConverter()</code> method. Some
 * non-standard converters are supplied in the {@link com.thoughtworks.xstream.converters.extended}
 * package and you can create your own by implementing the
 * {@link com.thoughtworks.xstream.converters.Converter} interface.
 * </p>
 * <p/>
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
 * The default converter, ie the converter which will be used if no other registered converter is
 * suitable, can be configured by either one of the constructors or can be changed using the
 * <code>changeDefaultConverter()</code> method. If not set, XStream uses
 * {@link com.thoughtworks.xstream.converters.reflection.ReflectionConverter} as the initial default
 * converter.
 * </p>
 * <p/>
 * <p>
 * <hr>
 * <b>Example</b><blockquote>
 * 
 * <pre>
 * xstream.changeDefaultConverter(new ACustomDefaultConverter());
 * </pre>
 * 
 * </blockquote>
 * <hr>
 * <p/>
 * <h3>Object graphs</h3>
 * <p/>
 * <p>
 * XStream has support for object graphs; a deserialized object graph will keep references intact,
 * including circular references.
 * </p>
 * <p/>
 * <p>
 * XStream can signify references in XML using either relative/absolute XPath or IDs. The mode can be changed using
 * <code>setMode()</code>:
 * </p>
 * <p/> <table border="1">
 * <tr>
 * <td><code>xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);</code></td>
 * <td><i>(Default)</i> Uses XPath relative references to signify duplicate references. This produces XML
 * with the least clutter.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);</code></td>
 * <td>Uses XPath absolute references to signify duplicate
 * references. This produces XML with the least clutter.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.ID_REFERENCES);</code></td>
 * <td>Uses ID references to signify duplicate references. In some scenarios, such as when using
 * hand-written XML, this is easier to work with.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.NO_REFERENCES);</code></td>
 * <td>This disables object graph support and treats the object structure like a tree. Duplicate
 * references are treated as two seperate objects and circular references cause an exception. This
 * is slightly faster and uses less memory than the other two modes.</td>
 * </tr>
 * </table>
 * <h3>Thread safety</h3>
 * <p>
 * The XStream instance is thread-safe. That is, once the XStream instance has been created and
 * configured, it may be shared across multiple threads allowing objects to be
 * serialized/deserialized concurrently.
 * <h3>Implicit collections</h3>
 * <p/>
 * <p>
 * To avoid the need for special tags for collections, you can define implicit collections using one
 * of the <code>addImplicitCollection</code> methods.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 */
public class XStream {

    // CAUTION: The sequence of the fields is intentional for an optimal XML output of a
    // self-serializaion!
    private ReflectionProvider reflectionProvider;
    private HierarchicalStreamDriver hierarchicalStreamDriver;
    private ClassLoaderReference classLoaderReference;
    private MarshallingStrategy marshallingStrategy;
    private Mapper mapper;
    private DefaultConverterLookup converterLookup;

    private ClassAliasingMapper classAliasingMapper;
    private FieldAliasingMapper fieldAliasingMapper;
    private AttributeAliasingMapper attributeAliasingMapper;
    private AttributeMapper attributeMapper;
    private DefaultImplementationsMapper defaultImplementationsMapper;
    private ImmutableTypesMapper immutableTypesMapper;
    private ImplicitCollectionMapper implicitCollectionMapper;

    private transient JVM jvm = new JVM();

    public static final int NO_REFERENCES = 1001;
    public static final int ID_REFERENCES = 1002;
    public static final int XPATH_RELATIVE_REFERENCES = 1003;
    public static final int XPATH_ABSOLUTE_REFERENCES = 1004;
    /**
     * @deprecated since 1.2, use {@value #XPATH_RELATIVE_REFERENCES} or
     *             {@value #XPATH_ABSOLUTE_REFERENCES} instead.
     */
    public static final int XPATH_REFERENCES = XPATH_RELATIVE_REFERENCES;

    public static final int PRIORITY_VERY_HIGH = 10000;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_LOW = -10;
    public static final int PRIORITY_VERY_LOW = -20;

    /**
     * Constructs a default XStream. The instance will use the {@link XppDriver} as default and tries to determin the best
     * match for the {@link ReflectionProvider} on its own.
     * 
     * @throws InitializationException in case of an initialization problem
     */
    public XStream() {
        this(null, (Mapper)null, new XppDriver());
    }

    /**
     * Constructs an XStream with a special {@link ReflectionProvider}. The instance will use the {@link XppDriver} as default.
     * 
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, (Mapper)null, new XppDriver());
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver}. The instance will tries to determin the best
     * match for the {@link ReflectionProvider} on its own.
     * 
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(null, (Mapper)null, hierarchicalStreamDriver);
    }
    
    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver} and {@link ReflectionProvider}.
     * 
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(
            ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, (Mapper)null, hierarchicalStreamDriver);
    }

    /**
     * @deprecated As of 1.2, use
     *             {@link #XStream(ReflectionProvider, Mapper, HierarchicalStreamDriver)}
     */
    public XStream(
            ReflectionProvider reflectionProvider, ClassMapper classMapper,
            HierarchicalStreamDriver driver) {
        this(reflectionProvider, (Mapper)classMapper, driver);
    }

    /**
     * @deprecated As of 1.2, use
     *             {@link #XStream(ReflectionProvider, Mapper, HierarchicalStreamDriver)} and
     *             register classAttributeIdentifier as alias
     */
    public XStream(
            ReflectionProvider reflectionProvider, ClassMapper classMapper,
            HierarchicalStreamDriver driver, String classAttributeIdentifier) {
        this(reflectionProvider, (Mapper)classMapper, driver);
        aliasAttribute(classAttributeIdentifier, "class");
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver} and {@link ReflectionProvider} and additionally with a prepared {@link Mapper}.
     * 
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(
            ReflectionProvider reflectionProvider, Mapper mapper, HierarchicalStreamDriver driver) {
        jvm = new JVM();
        if (reflectionProvider == null) {
            reflectionProvider = jvm.bestReflectionProvider();
        }
        this.reflectionProvider = reflectionProvider;
        this.hierarchicalStreamDriver = driver;
        this.classLoaderReference = new ClassLoaderReference(new CompositeClassLoader());
        this.mapper = mapper == null ? buildMapper(true) : mapper;
        this.converterLookup = new DefaultConverterLookup(this.mapper);

        setupMappers();
        setupAliases();
        setupDefaultImplementations();
        setupConverters();
        setupImmutableTypes();
        setMode(XPATH_RELATIVE_REFERENCES);
    }
    
    private Mapper buildMapper(boolean useXmlFriendlyMapper) {
        Mapper mapper = new DefaultMapper(classLoaderReference);
        if ( useXmlFriendlyMapper ){
            mapper = new XStream11XmlFriendlyMapper(mapper);
        }
        mapper = new ClassAliasingMapper(mapper);
        mapper = new FieldAliasingMapper(mapper);
        mapper = new AttributeAliasingMapper(mapper);
        mapper = new AttributeMapper(mapper);
        mapper = new ImplicitCollectionMapper(mapper);
        if (jvm.loadClass("net.sf.cglib.proxy.Enhancer") != null) {
           mapper = buildMapperDynamically(
                    "com.thoughtworks.xstream.mapper.CGLIBMapper", 
                    new Class[]{Mapper.class}, new Object[]{mapper});
        }
        mapper = new DynamicProxyMapper(mapper);
        if (JVM.is15()) {
            mapper = new EnumMapper(mapper);
        }
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        mapper = wrapMapper((MapperWrapper)mapper);
        mapper = new CachingMapper(mapper);
        return mapper;
    }

    private Mapper buildMapperDynamically(
            String className, Class[] constructorParamTypes,
            Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, classLoaderReference.getReference());
            Constructor constructor = type.getConstructor(constructorParamTypes);
            return (Mapper)constructor.newInstance(constructorParamValues);
        } catch (Exception e) {
            throw new InitializationException("Could not instatiate mapper : " + className, e);
        }
    }

    protected MapperWrapper wrapMapper(MapperWrapper next) {
        return next;
    }

    private void setupMappers() {
        classAliasingMapper = (ClassAliasingMapper)this.mapper
                .lookupMapperOfType(ClassAliasingMapper.class);
        fieldAliasingMapper = (FieldAliasingMapper)this.mapper
                .lookupMapperOfType(FieldAliasingMapper.class);
        attributeMapper = (AttributeMapper)this.mapper.lookupMapperOfType(AttributeMapper.class);
        attributeAliasingMapper = (AttributeAliasingMapper)this.mapper
                .lookupMapperOfType(AttributeAliasingMapper.class);
        implicitCollectionMapper = (ImplicitCollectionMapper)this.mapper
                .lookupMapperOfType(ImplicitCollectionMapper.class);
        defaultImplementationsMapper = (DefaultImplementationsMapper)this.mapper
                .lookupMapperOfType(DefaultImplementationsMapper.class);
        immutableTypesMapper = (ImmutableTypesMapper)this.mapper
                .lookupMapperOfType(ImmutableTypesMapper.class);

        // should use ctor, but converterLookup is not yet initialized instantiating this mapper
        if (attributeMapper != null) {
            attributeMapper.setConverterLookup(converterLookup);
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
        alias("date", Date.class);
        alias("url", URL.class);
        alias("bit-set", BitSet.class);

        alias("map", Map.class);
        alias("entry", Map.Entry.class);
        alias("properties", Properties.class);
        alias("list", List.class);
        alias("set", Set.class);

        alias("linked-list", LinkedList.class);
        alias("vector", Vector.class);
        alias("tree-map", TreeMap.class);
        alias("tree-set", TreeSet.class);
        alias("hashtable", Hashtable.class);

        // Instantiating these two classes starts the AWT system, which is undesirable. Calling
        // loadClass ensures a reference to the class is found but they are not instantiated.
        alias("awt-color", jvm.loadClass("java.awt.Color"));
        alias("awt-font", jvm.loadClass("java.awt.Font"));
        alias("awt-text-attribute", TextAttribute.class);

        alias("sql-timestamp", Timestamp.class);
        alias("sql-time", Time.class);
        alias("sql-date", java.sql.Date.class);
        alias("file", File.class);
        alias("locale", Locale.class);
        alias("gregorian-calendar", Calendar.class);

        // since jdk 1.4 included, but previously available as separate package ...
        Class type = jvm.loadClass("javax.security.auth.Subject");
        if (type != null) {
            alias("auth-subject", type);
        }

        if (JVM.is14()) {
            alias("linked-hash-map", jvm.loadClass("java.util.LinkedHashMap"));
            alias("linked-hash-set", jvm.loadClass("java.util.LinkedHashSet"));
            alias("trace", jvm.loadClass("java.lang.StackTraceElement"));
            alias("currency", jvm.loadClass("java.util.Currency"));
            aliasType("charset", jvm.loadClass("java.nio.charset.Charset"));
        }

        if (JVM.is15()) {
            alias("enum-set", jvm.loadClass("java.util.EnumSet"));
            alias("enum-map", jvm.loadClass("java.util.EnumMap"));
        }
    }

    protected void setupDefaultImplementations() {
        if (defaultImplementationsMapper == null) {
            return;
        }
        addDefaultImplementation(HashMap.class, Map.class);
        addDefaultImplementation(ArrayList.class, List.class);
        addDefaultImplementation(HashSet.class, Set.class);
        addDefaultImplementation(GregorianCalendar.class, Calendar.class);
    }

    protected void setupConverters() {
        // use different ReflectionProvider depending on JDK
        final ReflectionConverter reflectionConverter;
        if (JVM.is15()) {
            Class annotationProvider = jvm
                    .loadClass("com.thoughtworks.xstream.annotations.AnnotationProvider");
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.annotations.AnnotationReflectionConverter",
                    PRIORITY_VERY_LOW, new Class[]{
                            Mapper.class, ReflectionProvider.class, annotationProvider},
                    new Object[]{
                            mapper, reflectionProvider,
                            reflectionProvider.newInstance(annotationProvider)});
            reflectionConverter = (ReflectionConverter)converterLookup
                    .lookupConverterForType(Object.class);
        } else {
            reflectionConverter = new ReflectionConverter(mapper, reflectionProvider);
            registerConverter(reflectionConverter, PRIORITY_VERY_LOW);
        }

        registerConverter(new SerializableConverter(mapper, reflectionProvider), PRIORITY_LOW);
        registerConverter(new ExternalizableConverter(mapper), PRIORITY_LOW);

        registerConverter(new NullConverter(), PRIORITY_VERY_HIGH);
        registerConverter(new IntConverter(), PRIORITY_NORMAL);
        registerConverter(new FloatConverter(), PRIORITY_NORMAL);
        registerConverter(new DoubleConverter(), PRIORITY_NORMAL);
        registerConverter(new LongConverter(), PRIORITY_NORMAL);
        registerConverter(new ShortConverter(), PRIORITY_NORMAL);
        registerConverter(new CharConverter(), PRIORITY_NORMAL);
        registerConverter(new BooleanConverter(), PRIORITY_NORMAL);
        registerConverter(new ByteConverter(), PRIORITY_NORMAL);

        registerConverter(new StringConverter(), PRIORITY_NORMAL);
        registerConverter(new StringBufferConverter(), PRIORITY_NORMAL);
        registerConverter(new DateConverter(), PRIORITY_NORMAL);
        registerConverter(new BitSetConverter(), PRIORITY_NORMAL);
        registerConverter(new URLConverter(), PRIORITY_NORMAL);
        registerConverter(new BigIntegerConverter(), PRIORITY_NORMAL);
        registerConverter(new BigDecimalConverter(), PRIORITY_NORMAL);

        registerConverter(new ArrayConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new CharArrayConverter(), PRIORITY_NORMAL);
        registerConverter(new CollectionConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new MapConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new TreeMapConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new TreeSetConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new PropertiesConverter(), PRIORITY_NORMAL);
        registerConverter(new EncodedByteArrayConverter(), PRIORITY_NORMAL);

        registerConverter(new FileConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlTimestampConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlDateConverter(), PRIORITY_NORMAL);
        registerConverter(new DynamicProxyConverter(mapper, classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaClassConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaMethodConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new FontConverter(), PRIORITY_NORMAL);
        registerConverter(new ColorConverter(), PRIORITY_NORMAL);
        registerConverter(new TextAttributeConverter(), PRIORITY_NORMAL);
        registerConverter(new LocaleConverter(), PRIORITY_NORMAL);
        registerConverter(new GregorianCalendarConverter(), PRIORITY_NORMAL);
        
        // since jdk 1.4 included, but previously available as separate package ...
        if (jvm.loadClass("javax.security.auth.Subject") != null) {
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.SubjectConverter",
                    PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
        }

        if (JVM.is14()) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.ThrowableConverter",
                    PRIORITY_NORMAL, new Class[]{Converter.class},
                    new Object[]{reflectionConverter});
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.StackTraceElementConverter",
                    PRIORITY_NORMAL, null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.CurrencyConverter",
                    PRIORITY_NORMAL, null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.RegexPatternConverter",
                    PRIORITY_NORMAL, new Class[]{Converter.class},
                    new Object[]{reflectionConverter});
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.CharsetConverter",
                    PRIORITY_NORMAL, null, null);
        }

        if (JVM.is15()) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumConverter", PRIORITY_NORMAL,
                    null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumSetConverter", PRIORITY_NORMAL,
                    new Class[]{Mapper.class}, new Object[]{mapper});
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumMapConverter", PRIORITY_NORMAL,
                    new Class[]{Mapper.class}, new Object[]{mapper});
        }
        
        if (jvm.loadClass("net.sf.cglib.proxy.Enhancer") != null) {
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.reflection.CGLIBEnhancedConverter",
                    PRIORITY_NORMAL, new Class[]{Mapper.class, ReflectionProvider.class}, 
                    new Object[]{mapper, reflectionProvider});
        }
        
        registerConverter(new SelfStreamingInstanceChecker(reflectionConverter, this), PRIORITY_NORMAL);
    }

    private void dynamicallyRegisterConverter(
            String className, int priority, Class[] constructorParamTypes,
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
            throw new InitializationException("Could not instatiate converter : " + className, e);
        }
    }

    protected void setupImmutableTypes() {
        if (immutableTypesMapper == null) {
            return;
        }

        // primitives are always immutable
        addImmutableType(boolean.class);
        addImmutableType(Boolean.class);
        addImmutableType(byte.class);
        addImmutableType(Byte.class);
        addImmutableType(char.class);
        addImmutableType(Character.class);
        addImmutableType(double.class);
        addImmutableType(Double.class);
        addImmutableType(float.class);
        addImmutableType(Float.class);
        addImmutableType(int.class);
        addImmutableType(Integer.class);
        addImmutableType(long.class);
        addImmutableType(Long.class);
        addImmutableType(short.class);
        addImmutableType(Short.class);

        // additional types
        addImmutableType(Mapper.Null.class);
        addImmutableType(BigDecimal.class);
        addImmutableType(BigInteger.class);
        addImmutableType(String.class);
        addImmutableType(URL.class);
        addImmutableType(File.class);
        addImmutableType(Class.class);
        addImmutableType(TextAttribute.class);
        
        if (JVM.is14()) {
            // late bound types - allows XStream to be compiled on earlier JDKs
            Class type = jvm.loadClass("com.thoughtworks.xstream.converters.extended.CharsetConverter");
            addImmutableType(type);
        }
    }

    public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    /**
     * Serialize an object to a pretty-printed XML String.
     * @throws BaseException if the object cannot be serialized
     */
    public String toXML(Object obj) {
        Writer writer = new StringWriter();
        toXML(obj, writer);
        return writer.toString();
    }

    /**
     * Serialize an object to the given Writer as pretty-printed XML.
     * @throws BaseException if the object cannot be serialized
     */
    public void toXML(Object obj, Writer out) {
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
        marshal(obj, writer);
        writer.flush();
    }

    /**
     * Serialize an object to the given OutputStream as pretty-printed XML.
     * @throws BaseException if the object cannot be serialized
     */
    public void toXML(Object obj, OutputStream out) {
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
        marshal(obj, writer);
        writer.flush();
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     * @throws BaseException if the object cannot be serialized
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer) {
        marshal(obj, writer, null);
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     * 
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If
     *            not present, XStream shall create one lazily as needed.
     * @throws BaseException if the object cannot be serialized
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, DataHolder dataHolder) {
        marshallingStrategy.marshal(writer, obj, converterLookup, mapper, dataHolder);
    }

    /**
     * Deserialize an object from an XML String.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(String xml) {
        return fromXML(new StringReader(xml));
    }

    /**
     * Deserialize an object from an XML Reader.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(Reader xml) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), null);
    }

    /**
     * Deserialize an object from an XML InputStream.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(InputStream input) {
        return unmarshal(hierarchicalStreamDriver.createReader(input), null);
    }

    /**
     * Deserialize an object from an XML String, populating the fields of the given root object
     * instead of instantiating a new one.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(String xml, Object root) {
        return fromXML(new StringReader(xml), root);
    }

    /**
     * Deserialize an object from an XML Reader, populating the fields of the given root object
     * instead of instantiating a new one.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(Reader xml, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), root);
    }

    /**
     * Deserialize an object from an XML InputStream, populating the fields of the given root object
     * instead of instantiating a new one.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object fromXML(InputStream xml, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), root);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     * @throws BaseException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader) {
        return unmarshal(reader, null, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML), populating the fields
     * of the given root object instead of instantiating a new one.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root) {
        return unmarshal(reader, root, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     * 
     * @param root If present, the passed in object will have its fields populated, as opposed to
     *            XStream creating a new instance.
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If
     *            not present, XStream shall create one lazily as needed.
     * @throws BaseException if the object cannot be deserialized
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root, DataHolder dataHolder) {
        return marshallingStrategy.unmarshal(root, reader, dataHolder, converterLookup, mapper);
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
            throw new InitializationException("No "
                    + ClassAliasingMapper.class.getName()
                    + " available");
        }
        classAliasingMapper.addClassAlias(name, type);
    }

    /**
     * Alias a type to a shorter name to be used in XML elements.
     * Any class that is assignable to this type will be aliased to the same name.
     * 
     * @param name Short name
     * @param type Type to be aliased
     * @since 1.2
     * @throws InitializationException if no {@link ClassAliasingMapper} is available
     */
    public void aliasType(String name, Class type) {
        if (classAliasingMapper == null) {
            throw new InitializationException("No "
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
     * @throws InitializationException if no {@link DefaultImplementationsMapper} or no {@link ClassAliasingMapper} is available
     */
    public void alias(String name, Class type, Class defaultImplementation) {
        alias(name, type);
        addDefaultImplementation(defaultImplementation, type);
    }

    /**
     * Create an alias for a field name.
     * 
     * @param alias the alias itself
     * @param type the type that declares the field
     * @param fieldName the name of the field
     * @throws InitializationException if no {@link FieldAliasingMapper} is available
     */
    public void aliasField(String alias, Class type, String fieldName) {
        if (fieldAliasingMapper == null) {
            throw new InitializationException("No "
                    + FieldAliasingMapper.class.getName()
                    + " available");
        }
        fieldAliasingMapper.addFieldAlias(alias, type, fieldName);
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
            throw new InitializationException("No "
                    + AttributeAliasingMapper.class.getName()
                    + " available");
        }
        attributeAliasingMapper.addAliasFor(attributeName, alias);
    }

    /**
     * Use an XML attribute for a field or a specific type.
     * 
     * @param fieldName the name of the field
     * @param type the Class of the type to be rendered as XML attribute
     * @throws InitializationException if no {@link AttributeMapper} is available
     * @since 1.2
     */
    public void useAttributeFor(String fieldName, Class type) {
        if (attributeMapper == null) {
            throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
        }
        attributeMapper.addAttributeFor(fieldName, type);
    }

    /**
     * Use an XML attribute for an arbotrary type.
     * 
     * @param type the Class of the type to be rendered as XML attribute
     * @throws InitializationException if no {@link AttributeMapper} is available
     * @since 1.2
     */
    public void useAttributeFor(Class type) {
        if (attributeMapper == null) {
            throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
        }
        attributeMapper.addAttributeFor(type);
    }

    /**
     * Associate a default implementation of a class with an object. Whenever XStream encounters an
     * instance of this type, it will use the default implementation instead. For example,
     * java.util.ArrayList is the default implementation of java.util.List.
     * 
     * @param defaultImplementation
     * @param ofType
     * @throws InitializationException if no {@link DefaultImplementationsMapper} is available
     */
    public void addDefaultImplementation(Class defaultImplementation, Class ofType) {
        if (defaultImplementationsMapper == null) {
            throw new InitializationException("No "
                    + DefaultImplementationsMapper.class.getName()
                    + " available");
        }
        defaultImplementationsMapper.addDefaultImplementation(defaultImplementation, ofType);
    }

    /**
     * Add immutable types. The value of the instances of these types will always be written into
     * the stream even if they appear multiple times.
     * @throws InitializationException if no {@link ImmutableTypesMapper} is available
     */
    public void addImmutableType(Class type) {
        if (immutableTypesMapper == null) {
            throw new InitializationException("No "
                    + ImmutableTypesMapper.class.getName()
                    + " available");
        }
        immutableTypesMapper.addImmutableType(type);
    }

    public void registerConverter(Converter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    public void registerConverter(Converter converter, int priority) {
        converterLookup.registerConverter(converter, priority);
    }

    public void registerConverter(SingleValueConverter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    public void registerConverter(SingleValueConverter converter, int priority) {
        converterLookup.registerConverter(new SingleValueConverterWrapper(converter), priority);
    }

    /**
     * @throws ClassCastException if mapper is not really a deprecated {@link ClassMapper} instance
     * @deprecated As of 1.2, use {@link #getMapper}
     */
    public ClassMapper getClassMapper() {
        return (ClassMapper)mapper;
    }

    /**
     * Retrieve the mapper. This is by default a chain of {@link MapperWrapper MapperWrappers}.
     * 
     * @return the mapper
     * @since 1.2
     */
    public Mapper getMapper() {
        return mapper;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }

    /**
     * Change mode for dealing with duplicate references. Valid valuse are
     * <code>XPATH_ABSOLUTE_REFERENCES</code>, <code>XPATH_RELATIVE_REFERENCES</code>,
     * <code>XStream.ID_REFERENCES</code> and <code>XStream.NO_REFERENCES</code>.
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
        default:
            throw new IllegalArgumentException("Unknown mode : " + mode);
        }
    }

    /**
     * Adds a default implicit collection which is used for any unmapped xml tag.
     * 
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be an
     *            <code>java.util.ArrayList</code>.
     */
    public void addImplicitCollection(Class ownerType, String fieldName) {
        if (implicitCollectionMapper == null) {
            throw new InitializationException("No "
                    + ImplicitCollectionMapper.class.getName()
                    + " available");
        }
        implicitCollectionMapper.add(ownerType, fieldName, null, Object.class);
    }

    /**
     * Adds implicit collection which is used for all items of the given itemType.
     * 
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be an
     *            <code>java.util.ArrayList</code>.
     * @param itemType type of the items to be part of this collection.
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available
     */
    public void addImplicitCollection(Class ownerType, String fieldName, Class itemType) {
        if (implicitCollectionMapper == null) {
            throw new InitializationException("No "
                    + ImplicitCollectionMapper.class.getName()
                    + " available");
        }
        implicitCollectionMapper.add(ownerType, fieldName, null, itemType);
    }

    /**
     * Adds implicit collection which is used for all items of the given element name defined by
     * itemFieldName.
     * 
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This field must be an
     *            <code>java.util.ArrayList</code>.
     * @param itemFieldName element name of the implicit collection
     * @param itemType item type to be aliases be the itemFieldName
     * @throws InitializationException if no {@link ImplicitCollectionMapper} is available
     */
    public void addImplicitCollection(
            Class ownerType, String fieldName, String itemFieldName, Class itemType) {
        if (implicitCollectionMapper == null) {
            throw new InitializationException("No "
                    + ImplicitCollectionMapper.class.getName()
                    + " available");
        }
        implicitCollectionMapper.add(ownerType, fieldName, itemFieldName, itemType);
    }

    public DataHolder newDataHolder() {
        return new MapBackedDataHolder();
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using
     * XStream.
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
        return createObjectOutputStream(new PrettyPrintWriter(writer), "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using
     * XStream.
     * <p>
     * To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.
     * </p>
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(HierarchicalStreamWriter writer)
            throws IOException {
        return createObjectOutputStream(writer, "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using
     * XStream.
     * 
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(Writer writer, String rootNodeName)
            throws IOException {
        return createObjectOutputStream(new PrettyPrintWriter(writer), rootNodeName);
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using
     * XStream.
     * <p>
     * Because an ObjectOutputStream can contain multiple items and XML only allows a single root
     * node, the stream must be written inside an enclosing node.
     * </p>
     * <p>
     * It is necessary to call ObjectOutputStream.close() when done, otherwise the stream will be
     * incomplete.
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
    public ObjectOutputStream createObjectOutputStream(
            final HierarchicalStreamWriter writer, String rootNodeName) throws IOException {
        final StatefulWriter statefulWriter = new StatefulWriter(writer);
        statefulWriter.startNode(rootNodeName, null);
        return new CustomObjectOutputStream(new CustomObjectOutputStream.StreamCallback() {
            public void writeToStream(Object object) {
                marshal(object, statefulWriter);
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
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using
     * XStream.
     * 
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.0.3
     */
    public ObjectInputStream createObjectInputStream(Reader xmlReader) throws IOException {
        return createObjectInputStream(hierarchicalStreamDriver.createReader(xmlReader));
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using
     * XStream.
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
    public ObjectInputStream createObjectInputStream(final HierarchicalStreamReader reader)
            throws IOException {
        return new CustomObjectInputStream(new CustomObjectInputStream.StreamCallback() {
            public Object readFromStream() throws EOFException {
                if (!reader.hasMoreChildren()) {
                    throw new EOFException();
                }
                reader.moveDown();
                Object result = unmarshal(reader);
                reader.moveUp();
                return result;
            }

            public Map readFieldsFromStream() throws IOException {
                throw new NotActiveException("not in call to readObject");
            }

            public void defaultReadObject() throws NotActiveException {
                throw new NotActiveException("not in call to readObject");
            }

            public void registerValidation(ObjectInputValidation validation, int priority)
                    throws NotActiveException {
                throw new NotActiveException("stream inactive");
            }

            public void close() {
                reader.close();
            }
        });
    }

    /**
     * Change the ClassLoader XStream uses to load classes.
     * 
     * @since 1.1.1
     */
    public void setClassLoader(ClassLoader classLoader) {
        classLoaderReference.setReference(classLoader);
    }

    /**
     * Change the ClassLoader XStream uses to load classes.
     * 
     * @since 1.1.1
     */
    public ClassLoader getClassLoader() {
        return classLoaderReference.getReference();
    }

    /**
     * Prevents a field from being serialized. To omit a field you must always provide the declaring
     * type and not necessarily the type that is converted.
     * 
     * @since 1.1.3
     * @throws InitializationException if no {@link FieldAliasingMapper} is available
     */
    public void omitField(Class type, String fieldName) {
        if (fieldAliasingMapper == null) {
            throw new InitializationException("No "
                    + FieldAliasingMapper.class.getName()
                    + " available");
        }
        fieldAliasingMapper.omitField(type, fieldName);
    }

    public static class InitializationException extends BaseException {
        public InitializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public InitializationException(String message) {
            super(message);
        }
    }

    private Object readResolve() {
        jvm = new JVM();
        return this;
    }
}
