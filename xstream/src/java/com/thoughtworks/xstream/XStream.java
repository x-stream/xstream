/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022 XStream Committers.
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
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormatSymbols;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.chrono.MinguoEra;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.chrono.ThaiBuddhistEra;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
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
import com.thoughtworks.xstream.converters.basic.StringBuilderConverter;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.converters.basic.URIConverter;
import com.thoughtworks.xstream.converters.basic.URLConverter;
import com.thoughtworks.xstream.converters.basic.UUIDConverter;
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
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.converters.enums.EnumMapConverter;
import com.thoughtworks.xstream.converters.enums.EnumSetConverter;
import com.thoughtworks.xstream.converters.extended.AtomicBooleanConverter;
import com.thoughtworks.xstream.converters.extended.AtomicIntegerConverter;
import com.thoughtworks.xstream.converters.extended.AtomicLongConverter;
import com.thoughtworks.xstream.converters.extended.AtomicReferenceConverter;
import com.thoughtworks.xstream.converters.extended.CharsetConverter;
import com.thoughtworks.xstream.converters.extended.ColorConverter;
import com.thoughtworks.xstream.converters.extended.CurrencyConverter;
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
import com.thoughtworks.xstream.converters.extended.OptionalConverter;
import com.thoughtworks.xstream.converters.extended.OptionalDoubleConverter;
import com.thoughtworks.xstream.converters.extended.OptionalIntConverter;
import com.thoughtworks.xstream.converters.extended.OptionalLongConverter;
import com.thoughtworks.xstream.converters.extended.PathConverter;
import com.thoughtworks.xstream.converters.extended.RegexPatternConverter;
import com.thoughtworks.xstream.converters.extended.SqlDateConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimeConverter;
import com.thoughtworks.xstream.converters.extended.SqlTimestampConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.extended.ThrowableConverter;
import com.thoughtworks.xstream.converters.reflection.ExternalizableConverter;
import com.thoughtworks.xstream.converters.reflection.LambdaConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.converters.time.ChronologyConverter;
import com.thoughtworks.xstream.converters.time.DurationConverter;
import com.thoughtworks.xstream.converters.time.HijrahDateConverter;
import com.thoughtworks.xstream.converters.time.InstantConverter;
import com.thoughtworks.xstream.converters.time.JapaneseDateConverter;
import com.thoughtworks.xstream.converters.time.JapaneseEraConverter;
import com.thoughtworks.xstream.converters.time.LocalDateConverter;
import com.thoughtworks.xstream.converters.time.LocalDateTimeConverter;
import com.thoughtworks.xstream.converters.time.LocalTimeConverter;
import com.thoughtworks.xstream.converters.time.MinguoDateConverter;
import com.thoughtworks.xstream.converters.time.MonthDayConverter;
import com.thoughtworks.xstream.converters.time.OffsetDateTimeConverter;
import com.thoughtworks.xstream.converters.time.OffsetTimeConverter;
import com.thoughtworks.xstream.converters.time.PeriodConverter;
import com.thoughtworks.xstream.converters.time.SystemClockConverter;
import com.thoughtworks.xstream.converters.time.ThaiBuddhistDateConverter;
import com.thoughtworks.xstream.converters.time.ValueRangeConverter;
import com.thoughtworks.xstream.converters.time.WeekFieldsConverter;
import com.thoughtworks.xstream.converters.time.YearConverter;
import com.thoughtworks.xstream.converters.time.YearMonthConverter;
import com.thoughtworks.xstream.converters.time.ZoneIdConverter;
import com.thoughtworks.xstream.converters.time.ZonedDateTimeConverter;
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
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StatefulWriter;
import com.thoughtworks.xstream.mapper.AnnotationMapper;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.ElementIgnoringMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LambdaMapper;
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
import com.thoughtworks.xstream.security.InputManipulationException;
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
    private int collectionUpdateLimit = 20;

    private final ReflectionProvider reflectionProvider;
    private final HierarchicalStreamDriver hierarchicalStreamDriver;
    private final ClassLoaderReference classLoaderReference;
    private MarshallingStrategy marshallingStrategy;
    private final ConverterLookup converterLookup;
    private final ConverterRegistry converterRegistry;
    private final Mapper mapper;

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
    private AnnotationMapper annotationMapper;

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

    public static final String COLLECTION_UPDATE_LIMIT = "XStreamCollectionUpdateLimit";
    public static final String COLLECTION_UPDATE_SECONDS = "XStreamCollectionUpdateSeconds";

    private static final Pattern IGNORE_ALL = Pattern.compile(".*");

    /**
     * Constructs a default XStream.
     * <p>
     * The instance will use the {@link com.thoughtworks.xstream.io.xml.XppDriver} as default and tries to determine the
     * best match for the {@link ReflectionProvider} on its own.
     * </p>
     *
     * @throws InitializationException in case of an initialization problem
     */
    public XStream() {
        this(DefaultDriver.create());
    }

    /**
     * Constructs an XStream with a special {@link ReflectionProvider}.
     * <p>
     * The instance will use the {@link com.thoughtworks.xstream.io.xml.XppDriver} as default.
     * </p>
     *
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching reflection provider
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(final ReflectionProvider reflectionProvider) {
        this(reflectionProvider, DefaultDriver.create());
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
    public XStream(final HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(null, hierarchicalStreamDriver);
    }

    /**
     * Constructs an XStream with a special {@link HierarchicalStreamDriver} and {@link ReflectionProvider}.
     *
     * @param reflectionProvider the reflection provider to use or <em>null</em> for best matching Provider
     * @param hierarchicalStreamDriver the driver instance
     * @throws InitializationException in case of an initialization problem
     */
    public XStream(
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, hierarchicalStreamDriver, new ClassLoaderReference(new CompositeClassLoader()));
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
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoaderReference classLoaderReference) {
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
    @Deprecated
    public XStream(
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoader classLoader) {
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
    @Deprecated
    public XStream(
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoader classLoader, final Mapper mapper) {
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
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoaderReference classLoaderReference, final Mapper mapper) {
        this(reflectionProvider, driver, classLoaderReference, mapper, new DefaultConverterLookup());
    }

    private XStream(
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoaderReference classLoader, final Mapper mapper,
            final DefaultConverterLookup defaultConverterLookup) {
        this(reflectionProvider, driver, classLoader, mapper, new ConverterLookup() {
            @Override
            public Converter lookupConverterForType(final Class<?> type) {
                return defaultConverterLookup.lookupConverterForType(type);
            }
        }, new ConverterRegistry() {
            @Override
            public void registerConverter(final Converter converter, final int priority) {
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
    @Deprecated
    public XStream(
            final ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoader classLoader, final Mapper mapper, final ConverterLookup converterLookup,
            final ConverterRegistry converterRegistry) {
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
            ReflectionProvider reflectionProvider, final HierarchicalStreamDriver driver,
            final ClassLoaderReference classLoaderReference, final Mapper mapper, final ConverterLookup converterLookup,
            final ConverterRegistry converterRegistry) {
        if (reflectionProvider == null) {
            reflectionProvider = JVM.newReflectionProvider();
        }
        this.reflectionProvider = reflectionProvider;
        hierarchicalStreamDriver = driver;
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
        mapper = new EnumMapper(mapper);
        mapper = new LocalConversionMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        mapper = new LambdaMapper(mapper);
        mapper = new SecurityMapper(mapper);
        mapper = new AnnotationMapper(mapper, converterRegistry, converterLookup, classLoaderReference,
            reflectionProvider);
        mapper = wrapMapper((MapperWrapper)mapper);
        mapper = new CachingMapper(mapper);
        return mapper;
    }

  //@formatter:off
    /*
    private Mapper buildMapperDynamically(final String className, final Class<?>[] constructorParamTypes,
            final Object[] constructorParamValues) {
        try {
            final Class<?> type = Class.forName(className, false, classLoaderReference.getReference());
            final Constructor<?> constructor = type.getConstructor(constructorParamTypes);
            return (Mapper)constructor.newInstance(constructorParamValues);
        } catch (final Exception | LinkageError e) {
            throw new InitializationException("Could not instantiate mapper : " + className, e);
        }
    }
    */
    //@formatter:on

    protected MapperWrapper wrapMapper(final MapperWrapper next) {
        return next;
    }

    /**
     * @deprecated As of 1.4.8
     */
    @Deprecated
    protected boolean useXStream11XmlFriendlyMapper() {
        return false;
    }

    private void setupMappers() {
        packageAliasingMapper = mapper.lookupMapperOfType(PackageAliasingMapper.class);
        classAliasingMapper = mapper.lookupMapperOfType(ClassAliasingMapper.class);
        elementIgnoringMapper = mapper.lookupMapperOfType(ElementIgnoringMapper.class);
        fieldAliasingMapper = mapper.lookupMapperOfType(FieldAliasingMapper.class);
        attributeMapper = mapper.lookupMapperOfType(AttributeMapper.class);
        attributeAliasingMapper = mapper.lookupMapperOfType(AttributeAliasingMapper.class);
        systemAttributeAliasingMapper = mapper.lookupMapperOfType(SystemAttributeAliasingMapper.class);
        implicitCollectionMapper = mapper.lookupMapperOfType(ImplicitCollectionMapper.class);
        defaultImplementationsMapper = mapper.lookupMapperOfType(DefaultImplementationsMapper.class);
        immutableTypesMapper = mapper.lookupMapperOfType(ImmutableTypesMapper.class);
        localConversionMapper = mapper.lookupMapperOfType(LocalConversionMapper.class);
        securityMapper = mapper.lookupMapperOfType(SecurityMapper.class);
        annotationMapper = mapper.lookupMapperOfType(AnnotationMapper.class);
    }

    protected void setupSecurity() {
        if (securityMapper == null) {
            return;
        }

        addPermission(NullPermission.NULL);
        addPermission(PrimitiveTypePermission.PRIMITIVES);
        addPermission(ArrayTypePermission.ARRAYS);
        addPermission(InterfaceTypePermission.INTERFACES);
        allowTypeHierarchy(Calendar.class);
        allowTypeHierarchy(Collection.class);
        allowTypeHierarchy(Enum.class);
        allowTypeHierarchy(Map.class);
        allowTypeHierarchy(Map.Entry.class);
        allowTypeHierarchy(Member.class);
        allowTypeHierarchy(Number.class);
        allowTypeHierarchy(Throwable.class);
        allowTypeHierarchy(TimeZone.class);
        allowTypeHierarchy(Path.class);

        final Set<Class<?>> types = new HashSet<>();
        types.addAll(Arrays.<Class<?>>asList(AtomicBoolean.class, AtomicInteger.class, AtomicLong.class,
            AtomicReference.class, BitSet.class, Charset.class, Class.class, Currency.class, Date.class,
            DecimalFormatSymbols.class, File.class, Locale.class, Object.class, Pattern.class, StackTraceElement.class,
            String.class, StringBuffer.class, StringBuilder.class, URL.class, URI.class, UUID.class));
        if (JVM.isSQLAvailable()) {
            types.add(JVM.loadClassForName("java.sql.Timestamp"));
            types.add(JVM.loadClassForName("java.sql.Time"));
            types.add(JVM.loadClassForName("java.sql.Date"));
        }
        
        allowTypeHierarchy(Clock.class);
        types.add(Duration.class);
        types.add(Instant.class);
        types.add(LocalDate.class);
        types.add(LocalDateTime.class);
        types.add(LocalTime.class);
        types.add(MonthDay.class);
        types.add(OffsetDateTime.class);
        types.add(OffsetTime.class);
        types.add(Period.class);
        types.add(JVM.loadClassForName("java.time.Ser"));
        types.add(Year.class);
        types.add(YearMonth.class);
        types.add(ZonedDateTime.class);
        allowTypeHierarchy(ZoneId.class);
        types.add(HijrahDate.class);
        types.add(JapaneseDate.class);
        types.add(JapaneseEra.class);
        types.add(MinguoDate.class);
        types.add(ThaiBuddhistDate.class);
        types.add(JVM.loadClassForName("java.time.chrono.Ser"));
        allowTypeHierarchy(Chronology.class);
        types.add(ValueRange.class);
        types.add(WeekFields.class);
        types.add(Optional.class);
        types.add(OptionalDouble.class);
        types.add(OptionalInt.class);
        types.add(OptionalLong.class);

        types.remove(null);
        allowTypes(types.toArray(new Class[types.size()]));
    }

    /**
     * Setup the security framework of a XStream instance.
     * <p>
     * This method was a pure helper method for XStream 1.4.10 to 1.4.17.  It initialized an XStream instance with a
     * whitelist of well-known and simply types of the Java runtime as it is done in XStream 1.4.18 by default.  This
     * method will do therefore nothing in XStream 1.4.18 or higher.
     * </p>
     *
     * @param xstream
     * @since 1.4.10
     * @deprecated As of 1.4.18
     */
    @Deprecated
    public static void setupDefaultSecurity(final XStream xstream) {
        // Do intentionally nothing
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

        alias("string", String.class);
        alias("string-buffer", StringBuffer.class);
        alias("string-builder", StringBuilder.class);
        alias("uuid", UUID.class);
        alias("java-class", Class.class);
        alias("method", Method.class);
        alias("constructor", Constructor.class);
        alias("field", Field.class);
        alias("date", Date.class);
        alias("gregorian-calendar", Calendar.class);
        alias("uri", URI.class);
        alias("url", URL.class);
        alias("file", File.class);
        alias("locale", Locale.class);
        alias("bit-set", BitSet.class);
        alias("trace", StackTraceElement.class);
        alias("currency", Currency.class);

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
        alias("linked-hash-map", LinkedHashMap.class);
        alias("linked-hash-set", LinkedHashSet.class);
        alias("concurrent-hash-map", ConcurrentHashMap.class);
        alias("atomic-boolean", AtomicBoolean.class);
        alias("atomic-int", AtomicInteger.class);
        alias("atomic-long", AtomicLong.class);
        alias("atomic-reference", AtomicReference.class);

        alias("enum-set", EnumSet.class);
        alias("enum-map", EnumMap.class);
        alias("empty-list", Collections.emptyList().getClass());
        alias("empty-map", Collections.emptyMap().getClass());
        alias("empty-set", Collections.emptySet().getClass());
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

        final Class<?> type = JVM.loadClassForName("javax.activation.ActivationDataFlavor");
        if (type != null) {
            alias("activation-data-flavor", type);
        }

        if (JVM.isSQLAvailable()) {
            alias("sql-timestamp", JVM.loadClassForName("java.sql.Timestamp"));
            alias("sql-time", JVM.loadClassForName("java.sql.Time"));
            alias("sql-date", JVM.loadClassForName("java.sql.Date"));
        }

        alias("fixed-clock", JVM.loadClassForName("java.time.Clock$FixedClock"));
        alias("offset-clock", JVM.loadClassForName("java.time.Clock$OffsetClock"));
        alias("system-clock", JVM.loadClassForName("java.time.Clock$SystemClock"));
        alias("tick-clock", JVM.loadClassForName("java.time.Clock$TickClock"));
        alias("day-of-week", DayOfWeek.class);
        alias("duration", Duration.class);
        alias("instant", Instant.class);
        alias("local-date", LocalDate.class);
        alias("local-date-time", LocalDateTime.class);
        alias("local-time", LocalTime.class);
        alias("month", Month.class);
        alias("month-day", MonthDay.class);
        alias("offset-date-time", OffsetDateTime.class);
        alias("offset-time", OffsetTime.class);
        alias("period", Period.class);
        alias("year", Year.class);
        alias("year-month", YearMonth.class);
        alias("zoned-date-time", ZonedDateTime.class);
        aliasType("zone-id", ZoneId.class);
        aliasType("chronology", Chronology.class);
        alias("hijrah-date", HijrahDate.class);
        alias("hijrah-era", HijrahEra.class);
        alias("japanese-date", JapaneseDate.class);
        alias("japanese-era", JapaneseEra.class);
        alias("minguo-date", MinguoDate.class);
        alias("minguo-era", MinguoEra.class);
        alias("thai-buddhist-date", ThaiBuddhistDate.class);
        alias("thai-buddhist-era", ThaiBuddhistEra.class);
        alias("chrono-field", ChronoField.class);
        alias("chrono-unit", ChronoUnit.class);
        alias("iso-field", JVM.loadClassForName("java.time.temporal.IsoFields$Field"));
        alias("iso-unit", JVM.loadClassForName("java.time.temporal.IsoFields$Unit"));
        alias("julian-field", JVM.loadClassForName("java.time.temporal.JulianFields$Field"));
        alias("temporal-value-range", ValueRange.class);
        alias("week-fields", WeekFields.class);
        alias("optional", Optional.class);
        alias("optional-double", OptionalDouble.class);
        alias("optional-int", OptionalInt.class);
        alias("optional-long", OptionalLong.class);
        alias("serialized-lambda", SerializedLambda.class);

        aliasType("charset", Charset.class);
        aliasType("path", Path.class);

        if (JVM.loadClassForName("javax.security.auth.Subject") != null) {
            aliasDynamically("auth-subject", "javax.security.auth.Subject");
        }
        if (JVM.loadClassForName("javax.xml.datatype.Duration") != null) {
            aliasDynamically("xml-duration", "javax.xml.datatype.Duration");
        }

    }

    private void aliasDynamically(final String alias, final String className) {
        final Class<?> type = JVM.loadClassForName(className);
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
        registerConverter(new StringBuilderConverter(), PRIORITY_NORMAL);
        registerConverter(new ThrowableConverter(mapper, converterLookup), PRIORITY_NORMAL);
        registerConverter(new StackTraceElementConverter(), PRIORITY_NORMAL);
        registerConverter(new DateConverter(), PRIORITY_NORMAL);
        registerConverter(new GregorianCalendarConverter(), PRIORITY_NORMAL);
        registerConverter(new RegexPatternConverter(), PRIORITY_NORMAL);
        registerConverter(new CurrencyConverter(), PRIORITY_NORMAL);
        registerConverter(new CharsetConverter(), PRIORITY_NORMAL);
        registerConverter(new LocaleConverter(), PRIORITY_NORMAL);
        registerConverter(new BitSetConverter(), PRIORITY_NORMAL);
        registerConverter(new UUIDConverter(), PRIORITY_NORMAL);
        registerConverter(new URIConverter(), PRIORITY_NORMAL);
        registerConverter(new URLConverter(), PRIORITY_NORMAL);
        registerConverter(new BigIntegerConverter(), PRIORITY_NORMAL);
        registerConverter(new BigDecimalConverter(), PRIORITY_NORMAL);
        registerConverter(new PathConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new AtomicBooleanConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new AtomicIntegerConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new AtomicLongConverter(), PRIORITY_NORMAL);
        registerConverter(new AtomicReferenceConverter(mapper), PRIORITY_NORMAL);

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
        registerConverter(new EnumConverter(), PRIORITY_NORMAL);
        registerConverter(new EnumSetConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new EnumMapConverter(mapper), PRIORITY_NORMAL);

        registerConverter(new FileConverter(), PRIORITY_NORMAL);
        if (JVM.isSQLAvailable()) {
            registerConverter(new SqlTimestampConverter(), PRIORITY_NORMAL);
            registerConverter(new SqlTimeConverter(), PRIORITY_NORMAL);
            registerConverter(new SqlDateConverter(), PRIORITY_NORMAL);
        }
        registerConverter(new ChronologyConverter(), PRIORITY_NORMAL);
        registerConverter(new DurationConverter(), PRIORITY_NORMAL);
        registerConverter(new HijrahDateConverter(), PRIORITY_NORMAL);
        registerConverter(new JapaneseDateConverter(), PRIORITY_NORMAL);
        registerConverter(new JapaneseEraConverter(), PRIORITY_NORMAL);
        registerConverter(new InstantConverter(), PRIORITY_NORMAL);
        registerConverter(new LocalDateConverter(), PRIORITY_NORMAL);
        registerConverter(new LocalDateTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new LocalTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new MinguoDateConverter(), PRIORITY_NORMAL);
        registerConverter(new MonthDayConverter(), PRIORITY_NORMAL);
        registerConverter(new OffsetDateTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new OffsetTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new PeriodConverter(), PRIORITY_NORMAL);
        registerConverter(new SystemClockConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new ThaiBuddhistDateConverter(), PRIORITY_NORMAL);
        registerConverter(new ValueRangeConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new WeekFieldsConverter(mapper), PRIORITY_NORMAL);
        registerConverter(new YearConverter(), PRIORITY_NORMAL);
        registerConverter(new YearMonthConverter(), PRIORITY_NORMAL);
        registerConverter(new ZonedDateTimeConverter(), PRIORITY_NORMAL);
        registerConverter(new ZoneIdConverter(), PRIORITY_NORMAL);
        registerConverter(new OptionalConverter(mapper), PRIORITY_NORMAL);
        registerConverter((Converter)new OptionalDoubleConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new OptionalIntConverter(), PRIORITY_NORMAL);
        registerConverter((Converter)new OptionalLongConverter(), PRIORITY_NORMAL);
        registerConverter(new DynamicProxyConverter(mapper, classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaClassConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaMethodConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new JavaFieldConverter(classLoaderReference), PRIORITY_NORMAL);
        registerConverter(new LambdaConverter(mapper, reflectionProvider, classLoaderReference), PRIORITY_NORMAL);

        if (JVM.isAWTAvailable()) {
            registerConverter(new FontConverter(mapper), PRIORITY_NORMAL);
            registerConverter(new ColorConverter(), PRIORITY_NORMAL);
            registerConverter(new TextAttributeConverter(), PRIORITY_NORMAL);
        }
        if (JVM.isSwingAvailable()) {
            registerConverter(new LookAndFeelConverter(mapper, reflectionProvider), PRIORITY_NORMAL);
        }

        if (JVM.loadClassForName("javax.security.auth.Subject") != null) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.SubjectConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
        }
        if (JVM.loadClassForName("javax.xml.datatype.Duration") != null) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.DurationConverter",
                PRIORITY_NORMAL, null, null);
        }
        if (JVM.loadClassForName("javax.activation.ActivationDataFlavor") != null) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.ActivationDataFlavorConverter",
                PRIORITY_NORMAL, null, null);
        }
        if (JVM.isVersion(14)) {
            registerConverterDynamically("com.thoughtworks.xstream.converters.extended.RecordConverter",
                PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
        }

        registerConverter(new SelfStreamingInstanceChecker(converterLookup, this), PRIORITY_NORMAL);
    }

    private void registerConverterDynamically(final String className, final int priority,
            final Class<?>[] constructorParamTypes, final Object[] constructorParamValues) {
        try {
            final Class<?> type = Class.forName(className, false, classLoaderReference.getReference());
            final Constructor<?> constructor = type.getConstructor(constructorParamTypes);
            final Object instance = constructor.newInstance(constructorParamValues);
            if (instance instanceof Converter) {
                registerConverter((Converter)instance, priority);
            } else if (instance instanceof SingleValueConverter) {
                registerConverter((SingleValueConverter)instance, priority);
            }
        } catch (final Exception | LinkageError e) {
            throw new InitializationException("Could not instantiate converter : " + className, e);
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
        addImmutableType(Paths.get(".").getClass(), false);

        if (JVM.isAWTAvailable()) {
            addImmutableTypeDynamically("java.awt.font.TextAttribute", false);
        }

        addImmutableType(UUID.class, false);
        addImmutableType(URI.class, false);
        addImmutableType(Charset.class, false);
        addImmutableType(Currency.class, false);
        addImmutableType(Collections.EMPTY_LIST.getClass(), false);
        addImmutableType(Collections.EMPTY_SET.getClass(), false);
        addImmutableType(Collections.EMPTY_MAP.getClass(), false);

        addImmutableType(Duration.class, false);
        addImmutableType(Instant.class, false);
        addImmutableType(LocalDate.class, false);
        addImmutableType(LocalDateTime.class, false);
        addImmutableType(LocalTime.class, false);
        addImmutableType(MonthDay.class, false);
        addImmutableType(OffsetDateTime.class, false);
        addImmutableType(OffsetTime.class, false);
        addImmutableType(Period.class, false);
        addImmutableType(Year.class, false);
        addImmutableType(YearMonth.class, false);
        addImmutableType(ZonedDateTime.class, false);
        addImmutableType(ZoneId.class, false);
        addImmutableType(ZoneOffset.class, false);
        addImmutableTypeDynamically("java.time.ZoneRegion", false);
        addImmutableType(HijrahChronology.class, false);
        addImmutableType(HijrahDate.class, false);
        addImmutableType(IsoChronology.class, false);
        addImmutableType(JapaneseChronology.class, false);
        addImmutableType(JapaneseDate.class, false);
        addImmutableType(JapaneseEra.class, false);
        addImmutableType(MinguoChronology.class, false);
        addImmutableType(MinguoDate.class, false);
        addImmutableType(ThaiBuddhistChronology.class, false);
        addImmutableType(ThaiBuddhistDate.class, false);
        addImmutableTypeDynamically("java.time.temporal.IsoFields$Field", false);
        addImmutableTypeDynamically("java.time.temporal.IsoFields$Unit", false);
        addImmutableTypeDynamically("java.time.temporal.JulianFields$Field", false);
        addImmutableType(OptionalDouble.class, false);
        addImmutableType(OptionalInt.class, false);
        addImmutableType(OptionalLong.class, false);
    }

    private void addImmutableTypeDynamically(final String className, final boolean isReferenceable) {
        final Class<?> type = JVM.loadClassForName(className);
        if (type != null) {
            addImmutableType(type, isReferenceable);
        }
    }

    /**
     * Setter for an arbitrary marshalling strategy.
     *
     * @param marshallingStrategy the implementation to use
     * @see #setMode(int)
     */
    public void setMarshallingStrategy(final MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    /**
     * Set time limit for adding elements to collections or maps.
     * 
     * Manipulated content may be used to create recursive hash code calculations or sort operations. An
     * {@link InputManipulationException} is thrown, if the summed up time to add elements to collections or maps
     * exceeds the provided limit.
     * 
     * Note, that the time to add an individual element is calculated in seconds, not milliseconds. However, attacks
     * typically use objects with exponential growing calculation times.
     * 
     * @param maxSeconds limit in seconds or 0 to disable check
     * @since 1.4.19
     */
    public void setCollectionUpdateLimit(final int maxSeconds) {
        collectionUpdateLimit = maxSeconds;
    }

    /**
     * Serialize an object to a pretty-printed XML String.
     *
     * @throws XStreamException if the object cannot be serialized
     */
    public String toXML(final Object obj) {
        final Writer writer = new StringWriter();
        toXML(obj, writer);
        return writer.toString();
    }

    /**
     * Serialize an object to the given Writer as pretty-printed XML. The Writer will be flushed afterwards and in case
     * of an exception.
     *
     * @throws XStreamException if the object cannot be serialized
     */
    public void toXML(final Object obj, final Writer out) {
        @SuppressWarnings("resource")
        final HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
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
    public void toXML(final Object obj, final OutputStream out) {
        @SuppressWarnings("resource")
        final HierarchicalStreamWriter writer = hierarchicalStreamDriver.createWriter(out);
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
    public void marshal(final Object obj, final HierarchicalStreamWriter writer) {
        marshal(obj, writer, null);
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     *
     * @param dataHolder Extra data you can use to pass to your converters. Use this as you want. If not present,
     *            XStream shall create one lazily as needed.
     * @throws XStreamException if the object cannot be serialized
     */
    public void marshal(final Object obj, final HierarchicalStreamWriter writer, final DataHolder dataHolder) {
        marshallingStrategy.marshal(writer, obj, converterLookup, mapper, dataHolder);
    }

    /**
     * Deserialize an object from an XML String.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public <T> T fromXML(final String xml) {
        return fromXML(new StringReader(xml));
    }

    /**
     * Deserialize an object from an XML Reader.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    @SuppressWarnings("resource")
    public <T> T fromXML(final Reader reader) {
        return unmarshal(hierarchicalStreamDriver.createReader(reader), null);
    }

    /**
     * Deserialize an object from an XML InputStream.
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    @SuppressWarnings("resource")
    public <T> T fromXML(final InputStream input) {
        return unmarshal(hierarchicalStreamDriver.createReader(input), null);
    }

    /**
     * Deserialize an object from a URL. Depending on the parser implementation, some might take the file path as
     * SystemId to resolve additional references.
     *
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public <T> T fromXML(final URL url) {
        return fromXML(url, null);
    }

    /**
     * Deserialize an object from a file. Depending on the parser implementation, some might take the file path as
     * SystemId to resolve additional references.
     *
     * @throws XStreamException if the object cannot be deserialized
     * @since 1.4
     */
    public <T> T fromXML(final File file) {
        return fromXML(file, null);
    }

    /**
     * Deserialize an object from an XML String, populating the fields of the given root object instead of instantiating
     * a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into
     * the raw memory area of the existing object. Use with care!
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public <T> T fromXML(final String xml, final T root) {
        return fromXML(new StringReader(xml), root);
    }

    /**
     * Deserialize an object from an XML Reader, populating the fields of the given root object instead of instantiating
     * a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write directly into
     * the raw memory area of the existing object. Use with care!
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    @SuppressWarnings("resource")
    public <T> T fromXML(final Reader xml, final T root) {
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
    public <T> T fromXML(final URL url, final T root) {
        try (HierarchicalStreamReader reader = hierarchicalStreamDriver.createReader(url)) {
            return unmarshal(reader, root);
        }
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
    public <T> T fromXML(final File file, final T root) {
        try (final HierarchicalStreamReader reader = hierarchicalStreamDriver.createReader(file)) {
            return unmarshal(reader, root);
        }
    }

    /**
     * Deserialize an object from an XML InputStream, populating the fields of the given root object instead of
     * instantiating a new one. Note, that this is a special use case! With the ReflectionConverter XStream will write
     * directly into the raw memory area of the existing object. Use with care!
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    @SuppressWarnings("resource")
    public <T> T fromXML(final InputStream input, final T root) {
        return unmarshal(hierarchicalStreamDriver.createReader(input), root);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML).
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public <T> T unmarshal(final HierarchicalStreamReader reader) {
        return unmarshal(reader, null, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML), populating the fields of the given root
     * object instead of instantiating a new one. Note, that this is a special use case! With the ReflectionConverter
     * XStream will write directly into the raw memory area of the existing object. Use with care!
     *
     * @throws XStreamException if the object cannot be deserialized
     */
    public <T> T unmarshal(final HierarchicalStreamReader reader, final T root) {
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
    public <T> T unmarshal(final HierarchicalStreamReader reader, final T root, DataHolder dataHolder) {
        try {
            if (collectionUpdateLimit > 0) {
                if (dataHolder == null) {
                    dataHolder = new MapBackedDataHolder();
                }
                dataHolder.put(COLLECTION_UPDATE_LIMIT, Integer.valueOf(collectionUpdateLimit));
                dataHolder.put(COLLECTION_UPDATE_SECONDS, Integer.valueOf(0));
            }

            @SuppressWarnings("unchecked")
            final T t = (T)marshallingStrategy.unmarshal(root, reader, dataHolder, converterLookup, mapper);
            return t;
        } catch (final StackOverflowError e) {
            throw new InputManipulationException("Possible Denial of Service attack by Stack Overflow");
        } catch (final ConversionException e) {
            final Package pkg = getClass().getPackage();
            final String version = pkg != null ? pkg.getImplementationVersion() : null;
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
    public void alias(final String name, final Class<?> type) {
        if (classAliasingMapper == null) {
            throw new InitializationException("No " + ClassAliasingMapper.class.getName() + " available");
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
    public void aliasType(final String name, final Class<?> type) {
        if (classAliasingMapper == null) {
            throw new InitializationException("No " + ClassAliasingMapper.class.getName() + " available");
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
    public void alias(final String name, final Class<?> type, final Class<?> defaultImplementation) {
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
    public void aliasPackage(final String name, final String pkgName) {
        if (packageAliasingMapper == null) {
            throw new InitializationException("No " + PackageAliasingMapper.class.getName() + " available");
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
    public void aliasField(final String alias, final Class<?> definedIn, final String fieldName) {
        if (fieldAliasingMapper == null) {
            throw new InitializationException("No " + FieldAliasingMapper.class.getName() + " available");
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
    public void aliasAttribute(final String alias, final String attributeName) {
        if (attributeAliasingMapper == null) {
            throw new InitializationException("No " + AttributeAliasingMapper.class.getName() + " available");
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
    public void aliasSystemAttribute(final String alias, final String systemAttributeName) {
        if (systemAttributeAliasingMapper == null) {
            throw new InitializationException("No " + SystemAttributeAliasingMapper.class.getName() + " available");
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
    public void aliasAttribute(final Class<?> definedIn, final String attributeName, final String alias) {
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
    public void useAttributeFor(final String fieldName, final Class<?> type) {
        if (attributeMapper == null) {
            throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
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
    public void useAttributeFor(final Class<?> definedIn, final String fieldName) {
        if (attributeMapper == null) {
            throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
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
    public void useAttributeFor(final Class<?> type) {
        if (attributeMapper == null) {
            throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
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
    public void addDefaultImplementation(final Class<?> defaultImplementation, final Class<?> ofType) {
        if (defaultImplementationsMapper == null) {
            throw new InitializationException("No " + DefaultImplementationsMapper.class.getName() + " available");
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
    @Deprecated
    public void addImmutableType(final Class<?> type) {
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
    public void addImmutableType(final Class<?> type, final boolean isReferenceable) {
        if (immutableTypesMapper == null) {
            throw new com.thoughtworks.xstream.InitializationException("No "
                + ImmutableTypesMapper.class.getName()
                + " available");
        }
        immutableTypesMapper.addImmutableType(type, isReferenceable);
    }

    /**
     * Register a converter with normal priority.
     *
     * @param converter the converter instance
     */
    public void registerConverter(final Converter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    /**
     * Register a converter with chosen priority.
     *
     * @param converter the converter instance
     * @param priority the converter priority
     */
    public void registerConverter(final Converter converter, final int priority) {
        if (converterRegistry != null) {
            converterRegistry.registerConverter(converter, priority);
        }
    }

    /**
     * Register a single value converter with normal priority.
     *
     * @param converter the single value converter instance
     */
    public void registerConverter(final SingleValueConverter converter) {
        registerConverter(converter, PRIORITY_NORMAL);
    }

    /**
     * Register a single converter with chosen priority.
     *
     * @param converter the single converter instance
     * @param priority the converter priority
     */
    public void registerConverter(final SingleValueConverter converter, final int priority) {
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
    public void registerLocalConverter(final Class<?> definedIn, final String fieldName, final Converter converter) {
        if (localConversionMapper == null) {
            throw new InitializationException("No " + LocalConversionMapper.class.getName() + " available");
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
    public void registerLocalConverter(final Class<?> definedIn, final String fieldName,
            final SingleValueConverter converter) {
        final Converter wrapper = new SingleValueConverterWrapper(converter);
        registerLocalConverter(definedIn, fieldName, wrapper);
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
     * <code>XPATH_RELATIVE_REFERENCES</code>, <code>XStream.ID_REFERENCES</code> and <code>XStream.NO_REFERENCES</code>
     * .
     *
     * @throws IllegalArgumentException if the mode is not one of the declared types
     * @see #setMarshallingStrategy(MarshallingStrategy)
     * @see #XPATH_ABSOLUTE_REFERENCES
     * @see #XPATH_RELATIVE_REFERENCES
     * @see #ID_REFERENCES
     * @see #NO_REFERENCES
     */
    public void setMode(final int mode) {
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
    public void addImplicitCollection(final Class<?> ownerType, final String fieldName) {
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
    public void addImplicitCollection(final Class<?> ownerType, final String fieldName, final Class<?> itemType) {
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
    public void addImplicitCollection(final Class<?> ownerType, final String fieldName, final String itemFieldName,
            final Class<?> itemType) {
        addImplicitMap(ownerType, fieldName, itemFieldName, itemType, null);
    }

    /**
     * Adds an implicit array.
     *
     * @param ownerType class owning the implicit array
     * @param fieldName name of the array field
     * @since 1.4
     */
    public void addImplicitArray(final Class<?> ownerType, final String fieldName) {
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
    public void addImplicitArray(final Class<?> ownerType, final String fieldName, final Class<?> itemType) {
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
    public void addImplicitArray(final Class<?> ownerType, final String fieldName, final String itemName) {
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
    public void addImplicitMap(final Class<?> ownerType, final String fieldName, final Class<?> itemType,
            final String keyFieldName) {
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
    public void addImplicitMap(final Class<?> ownerType, final String fieldName, final String itemName,
            final Class<?> itemType, final String keyFieldName) {
        if (implicitCollectionMapper == null) {
            throw new InitializationException("No " + ImplicitCollectionMapper.class.getName() + " available");
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
    @SuppressWarnings("resource")
    public ObjectOutputStream createObjectOutputStream(final Writer writer) throws IOException {
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
    public ObjectOutputStream createObjectOutputStream(final HierarchicalStreamWriter writer) throws IOException {
        return createObjectOutputStream(writer, "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the writer using XStream.
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.0.3
     */
    @SuppressWarnings("resource")
    public ObjectOutputStream createObjectOutputStream(final Writer writer, final String rootNodeName)
            throws IOException {
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
    @SuppressWarnings("resource")
    public ObjectOutputStream createObjectOutputStream(final OutputStream out) throws IOException {
        return createObjectOutputStream(hierarchicalStreamDriver.createWriter(out), "object-stream");
    }

    /**
     * Creates an ObjectOutputStream that serializes a stream of objects to the OutputStream using XStream.
     *
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @since 1.3
     */
    @SuppressWarnings("resource")
    public ObjectOutputStream createObjectOutputStream(final OutputStream out, final String rootNodeName)
            throws IOException {
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
    @SuppressWarnings("resource")
    public ObjectOutputStream createObjectOutputStream(final HierarchicalStreamWriter writer, final String rootNodeName,
            final DataHolder dataHolder)
            throws IOException {
        final DataHolder context = dataHolder != null ? dataHolder : new MapBackedDataHolder();
        final StatefulWriter statefulWriter = new StatefulWriter(writer);
        statefulWriter.startNode(rootNodeName, null);
        return new CustomObjectOutputStream(context, new CustomObjectOutputStream.StreamCallback() {
            @Override
            public void writeToStream(final Object object) {
                marshal(object, statefulWriter, context);
            }

            @Override
            public void writeFieldsToStream(final Map<String, Object> fields) throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            @Override
            public void defaultWriteObject() throws NotActiveException {
                throw new NotActiveException("not in call to writeObject");
            }

            @Override
            public void flush() {
                statefulWriter.flush();
            }

            @Override
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
    @SuppressWarnings("resource")
    public ObjectInputStream createObjectInputStream(final Reader xmlReader) throws IOException {
        return createObjectInputStream(hierarchicalStreamDriver.createReader(xmlReader));
    }

    /**
     * Creates an ObjectInputStream that deserializes a stream of objects from an InputStream using XStream.
     *
     * @see #createObjectInputStream(com.thoughtworks.xstream.io.HierarchicalStreamReader)
     * @see #createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since 1.3
     */
    @SuppressWarnings("resource")
    public ObjectInputStream createObjectInputStream(final InputStream in) throws IOException {
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
    public ObjectInputStream createObjectInputStream(final HierarchicalStreamReader reader, DataHolder dataHolder)
            throws IOException {
        if (collectionUpdateLimit > 0) {
            if (dataHolder == null) {
                dataHolder = new MapBackedDataHolder();
            }
            dataHolder.put(COLLECTION_UPDATE_LIMIT, Integer.valueOf(collectionUpdateLimit));
            dataHolder.put(COLLECTION_UPDATE_SECONDS, Integer.valueOf(0));
        }
        final DataHolder dh = dataHolder;
        return new CustomObjectInputStream(new CustomObjectInputStream.StreamCallback() {
            @Override
            public Object readFromStream() throws EOFException {
                if (!reader.hasMoreChildren()) {
                    throw new EOFException();
                }
                reader.moveDown();
                final Object result = unmarshal(reader, null, dh);
                reader.moveUp();
                return result;
            }

            @Override
            public Map<String, Object> readFieldsFromStream() throws IOException {
                throw new NotActiveException("not in call to readObject");
            }

            @Override
            public void defaultReadObject() throws NotActiveException {
                throw new NotActiveException("not in call to readObject");
            }

            @Override
            public void registerValidation(final ObjectInputValidation validation, final int priority)
                    throws NotActiveException {
                throw new NotActiveException("stream inactive");
            }

            @Override
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
    public void setClassLoader(final ClassLoader classLoader) {
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
    public void omitField(final Class<?> definedIn, final String fieldName) {
        if (elementIgnoringMapper == null) {
            throw new InitializationException("No " + ElementIgnoringMapper.class.getName() + " available");
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
    public void ignoreUnknownElements(final String pattern) {
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
            throw new InitializationException("No " + ElementIgnoringMapper.class.getName() + " available");
        }
        elementIgnoringMapper.addElementsToIgnore(pattern);
    }

    /**
     * Process the annotations of the given types and configure the XStream.
     *
     * @param types the types with XStream annotations
     * @since 1.3
     */
    public void processAnnotations(final Class<?>... types) {
        if (annotationMapper == null) {
            throw new InitializationException("No " + AnnotationMapper.class.getName() + " available");
        }
        annotationMapper.processAnnotations(types);
    }

    /**
     * Set the auto-detection mode of the AnnotationMapper. Note that auto-detection implies that the XStream is
     * configured while it is processing the XML steams. This is a potential concurrency problem. Also is it technically
     * not possible to detect all class aliases at deserialization. You have been warned!
     *
     * @param mode <code>true</code> if annotations are auto-detected
     * @since 1.3
     */
    public void autodetectAnnotations(final boolean mode) {
        if (annotationMapper != null) {
            annotationMapper.autodetectAnnotations(mode);
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
    public void addPermission(final TypePermission permission) {
        if (securityMapper != null) {
            securityMapper.addPermission(permission);
        }
    }

    /**
     * Add security permission for explicit types by name.
     *
     * @param names the type names to allow
     * @since 1.4.7
     */
    public void allowTypes(final String... names) {
        addPermission(new ExplicitTypePermission(names));
    }

    /**
     * Add security permission for explicit types.
     *
     * @param types the types to allow
     * @since 1.4.7
     */
    public void allowTypes(final Class<?>... types) {
        addPermission(new ExplicitTypePermission(types));
    }

    /**
     * Add security permission for a type hierarchy.
     *
     * @param type the base type to allow
     * @since 1.4.7
     */
    public void allowTypeHierarchy(final Class<?> type) {
        addPermission(new TypeHierarchyPermission(type));
    }

    /**
     * Add security permission for types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to allow type names
     * @since 1.4.7
     */
    public void allowTypesByRegExp(final String... regexps) {
        addPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission for types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to allow type names
     * @since 1.4.7
     */
    public void allowTypesByRegExp(final Pattern... regexps) {
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
    public void allowTypesByWildcard(final String... patterns) {
        addPermission(new WildcardTypePermission(patterns));
    }

    /**
     * Add security permission denying another one.
     *
     * @param permission the permission to deny
     * @since 1.4.7
     */
    public void denyPermission(final TypePermission permission) {
        addPermission(new NoPermission(permission));
    }

    /**
     * Add security permission forbidding explicit types by name.
     *
     * @param names the type names to forbid
     * @since 1.4.7
     */
    public void denyTypes(final String... names) {
        denyPermission(new ExplicitTypePermission(names));
    }

    /**
     * Add security permission forbidding explicit types.
     *
     * @param types the types to forbid
     * @since 1.4.7
     */
    public void denyTypes(final Class<?>... types) {
        denyPermission(new ExplicitTypePermission(types));
    }

    /**
     * Add security permission forbidding a type hierarchy.
     *
     * @param type the base type to forbid
     * @since 1.4.7
     */
    public void denyTypeHierarchy(final Class<?> type) {
        denyPermission(new TypeHierarchyPermission(type));
    }

    /**
     * Add security permission forbidding types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to forbid type names
     * @since 1.4.7
     */
    public void denyTypesByRegExp(final String... regexps) {
        denyPermission(new RegExpTypePermission(regexps));
    }

    /**
     * Add security permission forbidding types matching one of the specified regular expressions.
     *
     * @param regexps the regular expressions to forbid type names
     * @since 1.4.7
     */
    public void denyTypesByRegExp(final Pattern... regexps) {
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
    public void denyTypesByWildcard(final String... patterns) {
        denyPermission(new WildcardTypePermission(patterns));
    }
}
