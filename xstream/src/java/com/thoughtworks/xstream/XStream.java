package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.basic.*;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.converters.extended.*;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.core.util.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.NamespaceAwareDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.io.NamespaceAwareDriver;
import com.thoughtworks.xstream.mapper.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * Simple facade to XStream library, a Java-XML serialization tool.
 * <p/>
 * <p><hr><b>Example</b><blockquote><pre>
 * XStream xstream = new XStream();
 * String xml = xstream.toXML(myObject); // serialize to XML
 * Object myObject2 = xstream.fromXML(xml); // deserialize from XML
 * </pre></blockquote><hr>
 * <p/>
 * <h3>Aliasing classes</h3>
 * <p/>
 * <p>To create shorter XML, you can specify aliases for classes using
 * the <code>alias()</code> method.
 * For example, you can shorten all occurences of element
 * <code>&lt;com.blah.MyThing&gt;</code> to
 * <code>&lt;my-thing&gt;</code> by registering an alias for the class.
 * <p><hr><blockquote><pre>
 * xstream.alias("my-thing", MyThing.class);
 * </pre></blockquote><hr>
 * <p/>
 * <h3>Converters</h3>
 * <p/>
 * <p>XStream contains a map of {@link com.thoughtworks.xstream.converters.Converter}
 * instances, each of which acts as a strategy for converting a particular type
 * of class to XML and back again. Out of the box, XStream contains converters
 * for most basic types (String, Date, int, boolean, etc) and collections (Map, List,
 * Set, Properties, etc). For other objects reflection is used to serialize
 * each field recursively.</p>
 * <p/>
 * <p>Extra converters can be registered using the <code>registerConverter()</code>
 * method. Some non-standard converters are supplied in the
 * {@link com.thoughtworks.xstream.converters.extended} package and you can create
 * your own by implementing the {@link com.thoughtworks.xstream.converters.Converter}
 * interface.</p>
 * <p/>
 * <p><hr><b>Example</b><blockquote><pre>
 * xstream.registerConverter(new SqlTimestampConverter());
 * xstream.registerConverter(new DynamicProxyConverter());
 * </pre></blockquote><hr>
 * <p>The default converter, ie the converter which will be used if no other registered 
 * converter is suitable, can be configured by either one of the constructors
 * or can be changed using the <code>changeDefaultConverter()</code> method.
 * If not set, XStream uses {@link com.thoughtworks.xstream.converters.reflection.ReflectionConverter}
 * as the initial default converter.
 * </p>
 * <p/>
 * <p><hr><b>Example</b><blockquote><pre>
 * xstream.changeDefaultConverter(new ACustomDefaultConverter());
 * </pre></blockquote><hr>
 * <p/>
 * <h3>Object graphs</h3>
 * <p/>
 * <p>XStream has support for object graphs; a deserialized object graph
 * will keep references intact, including circular references.</p>
 * <p/>
 * <p>XStream can signify references in XML using either XPath or IDs. The
 * mode can be changed using <code>setMode()</code>:</p>
 * <p/>
 * <table border="1">
 * <tr>
 * <td><code>xstream.setMode(XStream.XPATH_REFERENCES);</code></td>
 * <td><i>(Default)</i> Uses XPath references to signify duplicate
 * references. This produces XML with the least clutter.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.ID_REFERENCES);</code></td>
 * <td>Uses ID references to signify duplicate references. In some
 * scenarios, such as when using hand-written XML, this is
 * easier to work with.</td>
 * </tr>
 * <tr>
 * <td><code>xstream.setMode(XStream.NO_REFERENCES);</code></td>
 * <td>This disables object graph support and treats the object
 * structure like a tree. Duplicate references are treated
 * as two seperate objects and circular references cause an
 * exception. This is slightly faster and uses less memory
 * than the other two modes.</td>
 * </tr>
 * </table>
 *
 * <h3>Thread safety</h3>
 *
 * <p>The XStream instance is thread-safe. That is, once the XStream instance
 * has been created and configured, it may be shared across multiple threads
 * allowing objects to be serialized/deserialized concurrently.
 *
 * <h3>Implicit collections</h3>
 * <p/>
 * <p>To avoid the need for special tags for collections, you can define implicit collections using one of the 
 * <code>addImplicitCollection</code> methods.</p>
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 */
public class XStream {

    private ClassAliasingMapper classAliasingMapper;
    private FieldAliasingMapper fieldAliasingMapper;
    private DefaultImplementationsMapper defaultImplementationsMapper;
    private ImmutableTypesMapper immutableTypesMapper;
    private ImplicitCollectionMapper implicitCollectionMapper;

    private ReflectionProvider reflectionProvider;
    private HierarchicalStreamDriver hierarchicalStreamDriver;
    private MarshallingStrategy marshallingStrategy;
    private ClassLoaderReference classLoaderReference; // TODO: Should be changeable

    private ClassMapper classMapper;
    private DefaultConverterLookup converterLookup;
    private JVM jvm = new JVM();

    public static final int NO_REFERENCES = 1001;
    public static final int ID_REFERENCES = 1002;
    public static final int XPATH_REFERENCES = 1003;

    private static final int PRIORITY_NORMAL = 0;
    private static final int PRIORITY_LOW = -10;
    private static final int PRIORITY_VERY_LOW = -20;

    public XStream() {
        this(null, null, new XppDriver());
    }

    /**
     * @deprecated As of XStream 1.1.1, a default Converter is unnecessary as you can register a Converter with an
     *             associated priority. Use an alternate constructor.
     */
    public XStream(Converter defaultConverter) {
        this(null, null, new XppDriver(), null);
        registerConverter(defaultConverter, PRIORITY_VERY_LOW);
    }
    
    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(null, null, hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, null, new XppDriver());
    }

    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, null, hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver) {
        this(reflectionProvider, classMapper, driver, null);
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver, String classAttributeIdentifier) {
        jvm = new JVM();
        if (reflectionProvider == null) {
            reflectionProvider = jvm.bestReflectionProvider();
        }
        this.reflectionProvider = reflectionProvider;
        this.hierarchicalStreamDriver = driver;
        this.classLoaderReference = new ClassLoaderReference(new CompositeClassLoader());
        this.classMapper = classMapper == null ? buildMapper(classAttributeIdentifier) : classMapper;
        converterLookup = new DefaultConverterLookup(this.classMapper);
        setupAliases();
        setupDefaultImplementations();
        setupConverters();
        setupImmutableTypes();
        setMode(XPATH_REFERENCES);
    }

    /**
     * @deprecated As of XStream 1.1.1, a default Converter is unnecessary as you can register a Converter with an
     *             associated priority. Use an alternate constructor.
     */
    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver, String classAttributeIdentifier, Converter defaultConverter) {
        this(reflectionProvider, classMapper, driver, classAttributeIdentifier);
        registerConverter(defaultConverter, PRIORITY_VERY_LOW);
    }

    private ClassMapper buildMapper(String classAttributeIdentifier) {
        MapperWrapper mapper = new DefaultMapper(classLoaderReference, classAttributeIdentifier);
        mapper = new XmlFriendlyMapper(mapper);
        mapper = new ClassAliasingMapper(mapper);
        classAliasingMapper = (ClassAliasingMapper) mapper; // need a reference to that one
        mapper = new FieldAliasingMapper(mapper);
        fieldAliasingMapper = (FieldAliasingMapper) mapper; // need a reference to that one
        mapper = new ImplicitCollectionMapper(mapper);
        implicitCollectionMapper = (ImplicitCollectionMapper)mapper; // need a reference to this one
        mapper = new DynamicProxyMapper(mapper);
        if (JVM.is15()) {
            mapper = new EnumMapper(mapper);
        }
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        defaultImplementationsMapper = (DefaultImplementationsMapper) mapper; // and that one
        mapper = new ImmutableTypesMapper(mapper);
        immutableTypesMapper = (ImmutableTypesMapper)mapper; // that one too
        mapper = wrapMapper(mapper);
        mapper = new CachingMapper(mapper);
        return mapper;
    }

    protected MapperWrapper wrapMapper(MapperWrapper next) {
        return next;
    }

    protected void setupAliases() {
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

        // Instantiating these two classes starts the AWT system, which is undesirable. Calling loadClass ensures
        // a reference to the class is found but they are not instantiated.
        alias("awt-color", jvm.loadClass("java.awt.Color"));
        alias("awt-font", jvm.loadClass("java.awt.Font"));

        alias("sql-timestamp", Timestamp.class);
        alias("sql-time", Time.class);
        alias("sql-date", java.sql.Date.class);
        alias("file", File.class);
        alias("locale", Locale.class);
        alias("gregorian-calendar", Calendar.class);

        if (JVM.is14()) {
            alias("linked-hash-map", jvm.loadClass("java.util.LinkedHashMap"));
            alias("linked-hash-set", jvm.loadClass("java.util.LinkedHashSet"));
            alias("trace", jvm.loadClass("java.lang.StackTraceElement"));
            alias("currency", jvm.loadClass("java.util.Currency"));
        }

        if (JVM.is15()) {
            alias("enum-set", jvm.loadClass("java.util.EnumSet"));
            alias("enum-map", jvm.loadClass("java.util.EnumMap"));
        }
    }

    protected void setupDefaultImplementations() {
        addDefaultImplementation(HashMap.class, Map.class);
        addDefaultImplementation(ArrayList.class, List.class);
        addDefaultImplementation(HashSet.class, Set.class);
        addDefaultImplementation(GregorianCalendar.class, Calendar.class);
    }

    protected void setupConverters() {
        ReflectionConverter reflectionConverter = new ReflectionConverter(classMapper, reflectionProvider);
        registerConverter(reflectionConverter, PRIORITY_VERY_LOW);

        registerConverter(new SerializableConverter(classMapper, reflectionProvider), PRIORITY_LOW);
        registerConverter(new ExternalizableConverter(classMapper), PRIORITY_LOW);

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

        registerConverter(new ArrayConverter(classMapper), PRIORITY_NORMAL);
        registerConverter(new CharArrayConverter(), PRIORITY_NORMAL);
        registerConverter(new CollectionConverter(classMapper), PRIORITY_NORMAL);
        registerConverter(new MapConverter(classMapper), PRIORITY_NORMAL);
        registerConverter(new TreeMapConverter(classMapper), PRIORITY_NORMAL);
        registerConverter(new TreeSetConverter(classMapper), PRIORITY_NORMAL);
        registerConverter(new PropertiesConverter(), PRIORITY_NORMAL);
        registerConverter(new EncodedByteArrayConverter(), PRIORITY_NORMAL);

        registerConverter(new FileConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlTimestampConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new SqlDateConverter(), PRIORITY_NORMAL);
        registerConverter(new DynamicProxyConverter(classMapper, classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaClassConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaMethodConverter(), PRIORITY_NORMAL);
        registerConverter(new FontConverter(), PRIORITY_NORMAL);
        registerConverter(new ColorConverter(), PRIORITY_NORMAL);
        registerConverter(new LocaleConverter(), PRIORITY_NORMAL);
        registerConverter(new GregorianCalendarConverter(), PRIORITY_NORMAL);

        if (JVM.is14()) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.ThrowableConverter", PRIORITY_NORMAL,
                    new Class[] {Converter.class} , new Object[] { reflectionConverter} );
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.StackTraceElementConverter", PRIORITY_NORMAL,
                    null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.CurrencyConverter", PRIORITY_NORMAL,
                    null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.extended.RegexPatternConverter", PRIORITY_NORMAL,
                    new Class[] {Converter.class} , new Object[] { reflectionConverter} );
        }

        if (JVM.is15()) {
            // late bound converters - allows XStream to be compiled on earlier JDKs
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumConverter", PRIORITY_NORMAL,
                    null, null);
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumSetConverter", PRIORITY_NORMAL,
                    new Class[] {Mapper.class}, new Object[] {classMapper});
            dynamicallyRegisterConverter(
                    "com.thoughtworks.xstream.converters.enums.EnumMapConverter", PRIORITY_NORMAL,
                    new Class[] {Mapper.class}, new Object[] {classMapper});
        }
    }

    private void dynamicallyRegisterConverter(String className, int priority,
                                                        Class[] constructorParamTypes, Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, classLoaderReference.getReference());
            Constructor constructor = type.getConstructor(constructorParamTypes);
            Converter converter = (Converter) constructor.newInstance(constructorParamValues);
            registerConverter(converter, priority);
        } catch (Exception e) {
            throw new InitializationException("Could not instatiate converter : " + className, e);
        }
    }

    protected void setupImmutableTypes() {
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
        addImmutableType(ClassMapper.Null.class);
        addImmutableType(BigDecimal.class);
        addImmutableType(BigInteger.class);
        addImmutableType(String.class);
        addImmutableType(URL.class);
        addImmutableType(File.class);
        addImmutableType(Class.class);
    }

    public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    /**
     * Serialize an object to a pretty-printed XML String.
     */
    public String toXML(Object obj) {
        Writer stringWriter = new StringWriter();
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(stringWriter);
        marshal(obj, writer);
        writer.flush();
        writer.close();
        return stringWriter.toString();
    }

    /**
     * Serialize an object to the given Writer as pretty-printed XML.
     */
    public void toXML(Object obj, Writer out) {
        HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
        marshal(obj, writer);
        writer.flush();
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer) {
        marshal(obj, writer, null);
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     *
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If not present, XStream
     *                   shall create one lazily as needed.
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer, DataHolder dataHolder) {
        marshallingStrategy.marshal(writer, obj, converterLookup, classMapper, dataHolder);
    }

    /**
     * Deserialize an object from an XML String.
     */
    public Object fromXML(String xml) {
        return fromXML(new StringReader(xml));
    }

    /**
     * Deserialize an object from an XML Reader.
     */
    public Object fromXML(Reader xml) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), null);
    }

    /**
     * Deserialize an object from an XML String,
     * populating the fields of the given root object instead of instantiating
     * a new one.
     */
    public Object fromXML(String xml, Object root) {
        return fromXML(new StringReader(xml), root);
    }

    /**
     * Deserialize an object from an XML Reader,
     * populating the fields of the given root object instead of instantiating
     * a new one.
     */
    public Object fromXML(Reader xml, Object root) {
        return unmarshal(hierarchicalStreamDriver.createReader(xml), root);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     */
    public Object unmarshal(HierarchicalStreamReader reader) {
        return unmarshal(reader, null, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML),
     * populating the fields of the given root object instead of instantiating
     * a new one.
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root) {
        return unmarshal(reader, root, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     *
     * @param root If present, the passed in object will have its fields populated, as opposed to XStream creating a
     *             new instance.
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If not present, XStream
     *                   shall create one lazily as needed.
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root, DataHolder dataHolder) {
        return marshallingStrategy.unmarshal(root, reader, dataHolder, converterLookup, classMapper);
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param name Short name
     * @param type  Type to be aliased
     */
    public void alias(String name, Class type) {
        classAliasingMapper.addClassAlias(name, type);
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param name                  Short name
     * @param type                  Type to be aliased
     * @param defaultImplementation Default implementation of type to use if no other specified.
     */
    public void alias(String name, Class type, Class defaultImplementation) {
        alias(name, type);
        addDefaultImplementation(defaultImplementation, type);
    }

    public void aliasField(String alias, Class type, String fieldName) {
        fieldAliasingMapper.addFieldAlias(alias, type, fieldName);
    }

    /**
     * Associate a default implementation of a class with an object. Whenever XStream encounters an instance of this
     * type, it will use the default implementation instead.
     *
     * For example, java.util.ArrayList is the default implementation of java.util.List.
     * @param defaultImplementation
     * @param ofType
     */
    public void addDefaultImplementation(Class defaultImplementation, Class ofType) {
        defaultImplementationsMapper.addDefaultImplementation(defaultImplementation, ofType);
    }

    public void addImmutableType(Class type) {
        immutableTypesMapper.addImmutableType(type);
    }

    /**
     * @deprecated As of 1.1.1 you should register a converter with the appropriate priority.
     */
    public void changeDefaultConverter(Converter defaultConverter) {
        registerConverter(defaultConverter, PRIORITY_VERY_LOW);
    }

    public void registerConverter(Converter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    public void registerConverter(Converter converter, int priority) {
        converterLookup.registerConverter(converter, priority);
    }

    public ClassMapper getClassMapper() {
        return classMapper;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }

    /**
     * Change mode for dealing with duplicate references.
     * Valid valuse are <code>XStream.XPATH_REFERENCES</code>,
     * <code>XStream.ID_REFERENCES</code> and <code>XStream.NO_REFERENCES</code>.
     *
     * @see #XPATH_REFERENCES
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
            case XPATH_REFERENCES:
                setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy());
                break;
            default:
                throw new IllegalArgumentException("Unknown mode : " + mode);
        }
    }

    /**
     * @deprecated Use addImplicitCollection() instead.
     */
    public void addDefaultCollection(Class ownerType, String fieldName) {
        addImplicitCollection(ownerType, fieldName);
    }

    /**
     * Adds a default implicit collection which is used for any unmapped xml tag.
     * 
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This filed must be an <code>java.util.ArrayList</code>.
     */
    public void addImplicitCollection(Class ownerType, String fieldName) {
        implicitCollectionMapper.add(ownerType, fieldName, null, Object.class);
    }

    /**
     * Adds implicit collection which is used for all items of the given itemType.
     *
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This filed must be an <code>java.util.ArrayList</code>.
     * @param itemType type of the items to be part of this collection. 
     */
    public void addImplicitCollection(Class ownerType, String fieldName, Class itemType) {
        implicitCollectionMapper.add(ownerType, fieldName, null, itemType);
    }

    /**
     * Adds implicit collection which is used for all items of the given element name defined by itemFieldName.
     *
     * @param ownerType class owning the implicit collection
     * @param fieldName name of the field in the ownerType. This filed must be an <code>java.util.ArrayList</code>.
     * @param itemFieldName element  name of the implicit collection
     * @param itemType item type to be aliases be the itemFieldName
     */
    public void addImplicitCollection(Class ownerType, String fieldName, String itemFieldName, Class itemType) {
        implicitCollectionMapper.add(ownerType, fieldName, itemFieldName, itemType);
    }

    public DataHolder newDataHolder() {
        return new MapBackedDataHolder();
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     *
     * <p>To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.</p>
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(Writer writer) throws IOException {
        return createObjectOutputStream(new PrettyPrintWriter(writer), "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     *
     * <p>To change the name of the root element (from &lt;object-stream&gt;), use
     * {@link #createObjectOutputStream(java.io.Writer, String)}.</p>
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
        return createObjectOutputStream(new PrettyPrintWriter(writer), rootNodeName);
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     *
     * <p>Because an ObjectOutputStream can contain multiple items and XML only allows a single root node, the stream
     * must be written inside an enclosing node.</p>
     *
     * <p>It is necessary to call ObjectOutputStream.close() when done, otherwise the stream will be incomplete.</p>
     *
     * <h3>Example</h3>
     * <pre>ObjectOutputStream out = xstream.createObjectOutputStream(aWriter, "things");
     * out.writeInt(123);
     * out.writeObject("Hello");
     * out.writeObject(someObject)
     * out.close();</pre>
     *
     * @param writer The writer to serialize the objects to.
     * @param rootNodeName The name of the root node enclosing the stream of objects.
     *
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    public ObjectOutputStream createObjectOutputStream(final HierarchicalStreamWriter writer, String rootNodeName) throws IOException {
        writer.startNode(rootNodeName);
        return new CustomObjectOutputStream(new CustomObjectOutputStream.StreamCallback() {
            public void writeToStream(Object object) {
                marshal(object, writer);
            }

            public void writeFieldsToStream(Map fields) throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            public void defaultWriteObject() throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            public void flush() {
                writer.flush();
            }

            public void close() {
                writer.endNode();
                writer.close();
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
     * Creates an ObjectInputStream that deserializes a stream of objects from a reader using XStream.
     *
     * <h3>Example</h3>
     * <pre>ObjectInputStream in = xstream.createObjectOutputStream(aReader);
     * int a = out.readInt();
     * Object b = out.readObject();
     * Object c = out.readObject();</pre>
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.0.3
     */
    public ObjectInputStream createObjectInputStream(final HierarchicalStreamReader reader) throws IOException {
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

            public void registerValidation(ObjectInputValidation validation, int priority) throws NotActiveException {
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

    public static class InitializationException extends BaseException {
        public InitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
