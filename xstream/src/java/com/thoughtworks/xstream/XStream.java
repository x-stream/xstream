package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

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
 * @author Joe Walnes
 */
public class XStream {

    private HierarchicalStreamDriver hierarchicalStreamDriver;
    private MarshallingStrategy marshallingStrategy;
    private ClassMapper classMapper;
    private DefaultConverterLookup converterLookup;

    public static final int NO_REFERENCES = 1001;
    public static final int ID_REFERENCES = 1002;
    public static final int XPATH_REFERENCES = 1003;

    public XStream() {
        this(JVM.bestReflectionProvider(), new DefaultClassMapper(), new XppDriver());
    }

    public XStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(JVM.bestReflectionProvider(), new DefaultClassMapper(), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider) {
        this(reflectionProvider, new DefaultClassMapper(), new XppDriver());
    }

    public XStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        this(reflectionProvider, new DefaultClassMapper(), hierarchicalStreamDriver);
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver) {
        this(reflectionProvider, classMapper, driver, "class");
    }

    public XStream(ReflectionProvider reflectionProvider, ClassMapper classMapper, HierarchicalStreamDriver driver, String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.hierarchicalStreamDriver = driver;
        setMode(XPATH_REFERENCES);
        converterLookup = new DefaultConverterLookup(reflectionProvider, classMapper, classAttributeIdentifier);
        converterLookup.setupDefaults();
    }

    public void setMarshallingStrategy(MarshallingStrategy marshallingStrategy) {
        this.marshallingStrategy = marshallingStrategy;
    }

    /**
     * Serialize an object to a pretty-printed XML String.
     */
    public String toXML(Object obj) {
        Writer stringWriter = new StringWriter();
        HierarchicalStreamWriter writer = new PrettyPrintWriter(stringWriter);
        marshal(obj, writer);
        return stringWriter.toString();
    }

    /**
     * Serialize an object to the given Writer as pretty-printed XML.
     */
    public void toXML(Object obj, Writer writer) {
        marshal(obj, new PrettyPrintWriter(writer));
    }

    /**
     * Serialize and object to a hierarchical data structure (such as XML).
     */
    public void marshal(Object obj, HierarchicalStreamWriter writer) {
        marshallingStrategy.marshal(writer, obj, converterLookup, classMapper);
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
     * Deserialize an object from a hierarchical data structure (such as XML).
     */
    public Object unmarshal(HierarchicalStreamReader reader) {
        return unmarshal(reader, null);
    }

    /**
     * Deserialize an object from a hierarchical data structure (such as XML),
     * populating the fields of the given root object instead of instantiating
     * a new one.
     */
    public Object unmarshal(HierarchicalStreamReader reader, Object root) {
        return marshallingStrategy.unmarshal(root, reader, converterLookup, classMapper);
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param elementName Short name
     * @param type        Type to be aliased
     */
    public void alias(String elementName, Class type) {
        converterLookup.alias(elementName, type, type);
    }

    /**
     * Alias a Class to a shorter name to be used in XML elements.
     *
     * @param elementName           Short name
     * @param type                  Type to be aliased
     * @param defaultImplementation Default implementation of type to use if no other specified.
     */
    public void alias(String elementName, Class type, Class defaultImplementation) {
        converterLookup.alias(elementName, type, defaultImplementation);
    }

    public void registerConverter(Converter converter) {
        converterLookup.registerConverter(converter);
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

    public void addDefaultCollection(Class type, String fieldName) {
        converterLookup.addDefaultCollection(type, fieldName);
    }
}
