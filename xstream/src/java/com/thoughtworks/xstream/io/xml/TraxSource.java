package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.XStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.xml.transform.sax.SAXSource;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link SAXSource JAXP TrAX Source} that enables using XStream
 * object serialization as direct input for XSLT processors without
 * resorting to an intermediate representation such as text XML, DOM
 * or DOM4J.
 * <p/>
 * The following example shows how to apply an XSL Transformation
 * to a set of Java objects gathered into a List
 * (<code>source</code>):</p>
 * <pre><code>
 *   public static String transform(List source, String stylesheet) {
 *     try {
 *       Transformer transformer = TransformerFactory.newInstance()
 *                             .newTransformer(new StreamSource(stylesheet));
 *       XStreamSource in = new XStreamSource(source);
 *       Writer out = new StringWriter();
 *       transformer.transform(in, new StreamResult(out));
 *       return out.toString();
 *     }
 *     catch (TransformerException e) {
 *       throw new RuntimeException("XSLT Transformation failed", e);
 *     }
 *   }
 * </code></pre>
 *
 * @author Laurent Bihanic
 */
public class TraxSource extends SAXSource {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns <code>true</code> when passed this value as an
     * argument, the Transformer natively supports XStream.
     * <p/>
     * <strong>Note</strong>: This implementation does not override
     * the {@link SAXSource#FEATURE} value defined by its superclass
     * to be considered as a SAXSource by Transformer implementations
     * not natively supporting this XStream-specific source
     * </p>
     */
    public final static String XSTREAM_FEATURE =
            "http://com.thoughtworks.xstream/XStreamSource/feature";

    /**
     * The XMLReader object associated to this source or
     * <code>null</code> if no XMLReader has yet been requested.
     *
     * @see #getXMLReader
     */
    private XMLReader xmlReader = null;

    /**
     * The configured XStream facade to use for serializing objects.
     */
    private XStream xstream = null;

    /**
     * The list of Java objects to be serialized.
     */
    private List source = null;


    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Creates a XStream TrAX source.
     */
    public TraxSource() {
        super(new InputSource());
    }

    /**
     * Creates a XStream TrAX source, specifying the object to
     * marshal.
     *
     * @param source the object to marshal.
     * @throws IllegalArgumentException if <code>source</code> is
     *                                  <code>null</code>.
     * @see #setSource(java.lang.Object)
     */
    public TraxSource(Object source) {
        super(new InputSource());

        this.setSource(source);
    }

    /**
     * Creates a XStream TrAX source, specifying the object to
     * marshal and a configured (with aliases) XStream facade.
     *
     * @param source  the object to marshal.
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>source</code> or
     *                                  <code>xstream</code> is <code>null</code>.
     * @see #setSource(java.lang.Object)
     * @see #setXStream(com.thoughtworks.xstream.XStream)
     */
    public TraxSource(Object source, XStream xstream) {
        super(new InputSource());

        this.setSource(source);
        this.setXStream(xstream);
    }

    /**
     * Creates a XStream TrAX source, setting the objects to
     * marshal.
     *
     * @param source the list of objects to marshal.
     * @throws IllegalArgumentException if <code>source</code> is
     *                                  <code>null</code> or empty.
     * @see #setSourceAsList(java.util.List)
     */
    public TraxSource(List source) {
        super(new InputSource());

        this.setSourceAsList(source);
    }

    /**
     * Creates a XStream TrAX source, setting the objects to
     * marshal and a configured (with aliases) XStream facade.
     *
     * @param source  the list of objects to marshal.
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>source</code> or
     *                                  <code>xstream</code> is <code>null</code> or
     *                                  <code>source</code> is empty.
     * @see #setSourceAsList(java.util.List)
     * @see #setXStream(com.thoughtworks.xstream.XStream)
     */
    public TraxSource(List source, XStream xstream) {
        super(new InputSource());

        this.setSourceAsList(source);
        this.setXStream(xstream);
    }

    //-------------------------------------------------------------------------
    // SAXSource overwritten methods
    //-------------------------------------------------------------------------

    /**
     * Sets the SAX InputSource to be used for the Source.
     * <p/>
     * As this implementation only supports object lists as data
     * source, this method always throws an
     * {@link UnsupportedOperationException}.
     * </p>
     *
     * @param inputSource a valid InputSource reference.
     * @throws UnsupportedOperationException always!
     */
    public void setInputSource(InputSource inputSource) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the XMLReader to be used for the Source.
     * <p/>
     * As this implementation only supports object lists as data
     * source, this method throws an
     * {@link UnsupportedOperationException} if the provided reader
     * object does not implement the SAX {@link XMLFilter}
     * interface.  Otherwise, a {@link SaxWriter} instance will be
     * attached as parent of the filter chain.</p>
     *
     * @param reader a valid XMLReader or XMLFilter reference.
     * @throws UnsupportedOperationException if <code>reader</code>
     *                                       is not a SAX
     *                                       {@link XMLFilter}.
     * @see #getXMLReader
     */
    public void setXMLReader(XMLReader reader) {
        this.createXMLReader(reader);
    }

    /**
     * Returns the XMLReader to be used for the Source.
     * <p/>
     * This implementation returns a specific XMLReader
     * ({@link SaxWriter}) generating the XML from a list of input
     * objects.
     * </p>
     *
     * @return an XMLReader generating the XML from a list of input
     *         objects.
     */
    public XMLReader getXMLReader() {
        if (this.xmlReader == null) {
            this.createXMLReader(null);
        }
        return this.xmlReader;
    }


    //-------------------------------------------------------------------------
    // Specific implementation
    //-------------------------------------------------------------------------

    /**
     * Sets the XStream facade to use when marshalling objects.
     *
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>xstream</code> is
     *                                  <code>null</code>.
     */
    public void setXStream(XStream xstream) {
        if (xstream == null) {
            throw new IllegalArgumentException("xstream");
        }
        this.xstream = xstream;

        this.configureXMLReader();
    }

    /**
     * Sets the object to marshal.
     *
     * @param obj the object to marshal.
     * @throws IllegalArgumentException if <code>source</code> is
     *                                  <code>null</code>.
     */
    public void setSource(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj");
        }
        List list = new ArrayList(1);
        list.add(obj);

        this.setSourceAsList(list);
    }

    /**
     * Sets the list of objects to marshal.
     * <p/>
     * When dealing with non-text input (such as SAX or DOM), XSLT
     * processors support multiple root node children for the source
     * tree (see
     * <a href="http://www.w3.org/TR/xslt#root-node-children">section 3.1</a>
     * of the &quot;XSL Transformations (XSLT) Version 1.0&quot;
     * specification.  Using a list of objects as source makes use
     * of this feature and allows creating XML documents merging
     * the XML serialization of several Java objects.
     *
     * @param list the list of objects to marshal.
     * @throws IllegalArgumentException if <code>source</code> is
     *                                  <code>null</code> or empty.
     */
    public void setSourceAsList(List list) {
        if ((list == null) || (list.isEmpty())) {
            throw new IllegalArgumentException("list");
        }
        this.source = list;

        this.configureXMLReader();
    }

    private void createXMLReader(XMLReader filterChain) {
        if (filterChain == null) {
            this.xmlReader = new SaxWriter();
        } else {
            if (filterChain instanceof XMLFilter) {
                // Connect the filter chain to a document reader.
                XMLFilter filter = (XMLFilter) filterChain;
                while (filter.getParent() instanceof XMLFilter) {
                    filter = (XMLFilter) (filter.getParent());
                }
                filter.setParent(new SaxWriter());

                // Read XML data from filter chain.
                this.xmlReader = filterChain;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        this.configureXMLReader();
    }

    private void configureXMLReader() {
        if (this.xmlReader != null) {
            try {
                if (this.xstream != null) {
                    this.xmlReader.setProperty(SaxWriter.CONFIGURED_XSTREAM_PROPERTY, this.xstream);
                }
                if (this.source != null) {
                    this.xmlReader.setProperty(SaxWriter.SOURCE_OBJECT_LIST_PROPERTY, this.source);
                }
            } catch (SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
}

