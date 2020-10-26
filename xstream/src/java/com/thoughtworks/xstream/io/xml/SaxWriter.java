/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2013, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A SAX {@link org.xml.sax.XMLReader parser} that acts as an XStream
 * {@link com.thoughtworks.xstream.io.HierarchicalStreamWriter} to enable direct generation of a SAX event flow from the
 * XStream serialization of a list of list of Java objects.
 * <p>
 * As a custom SAX parser, this class ignores the arguments of the two standard parse methods (
 * {@link #parse(java.lang.String)} and {@link #parse(org.xml.sax.InputSource)}) but relies on a proprietary SAX
 * property {@link #SOURCE_OBJECT_QUEUE_PROPERTY} to define the list of objects to serialize.
 * </p>
 * <p>
 * Configuration of this SAX parser is achieved through the standard {@link #setProperty SAX property mechanism}. While
 * specific setter methods require direct access to the parser instance, SAX properties support configuration settings
 * to be propagated through a chain of {@link org.xml.sax.XMLFilter filters} down to the underlying parser object.
 * </p>
 * <p>
 * This mechanism shall be used to configure the {@link #SOURCE_OBJECT_QUEUE_PROPERTY objects to be serialized} as well
 * as the {@link #CONFIGURED_XSTREAM_PROPERTY XStream facade}.
 * </p>
 *
 * @author Laurent Bihanic
 * @author Joerg Schaible
 */
public final class SaxWriter extends AbstractXmlWriter implements XMLReader {
    /**
     * The {@link #setProperty SAX property} to configure the XStream facade to be used for object serialization. If the
     * property is not set, a new XStream facade will be allocated for each parse.
     */
    public final static String CONFIGURED_XSTREAM_PROPERTY =
            "http://com.thoughtworks.xstream/sax/property/configured-xstream";

    /**
     * The {@link #setProperty SAX property} to configure a list of Java objects to serialize. Setting this property
     * prior invoking one of the parse() methods is mandatory.
     *
     * @deprecated As of upcoming use {@link #SOURCE_OBJECT_QUEUE_PROPERTY} instead
     */
    @Deprecated
    public final static String SOURCE_OBJECT_LIST_PROPERTY =
            "http://com.thoughtworks.xstream/sax/property/source-object-list";

    /**
     * The {@link #setProperty SAX property} to configure a queue of Java objects to serialize. Setting this property
     * prior invoking one of the parse() methods is mandatory.
     *
     * @see #parse(java.lang.String)
     * @see #parse(org.xml.sax.InputSource)
     */
    public final static String SOURCE_OBJECT_QUEUE_PROPERTY =
            "http://com.thoughtworks.xstream/sax/property/source-object-queue";

    /**
     * The {@link #setProperty SAX property} to provide a Supplier for a CustomObjectOutputStream used to write objects
     * to marshal into a BlockingQueue.
     *
     * @see #parse(java.lang.String)
     * @see #parse(org.xml.sax.InputSource)
     */
    public final static String OOS_SUPPLIER_PROPERTY =
            "http://com.thoughtworks.xstream/sax/property/custom-oos-supplier";

    final static Serializable EOS = new Mapper.Null();

    // =========================================================================
    // SAX XMLReader interface support
    // =========================================================================

    /**
     * The SAX EntityResolver associated to this XMLReader.
     */
    private EntityResolver entityResolver = null;

    /**
     * The SAX DTDHandler associated to this XMLReader.
     */
    private DTDHandler dtdHandler = null;

    /**
     * The SAX ContentHandler associated to this XMLReader.
     */
    private ContentHandler contentHandler = null;

    /**
     * The SAX ErrorHandler associated to this XMLReader.
     */
    private ErrorHandler errorHandler = null;

    /**
     * The SAX features defined for this XMLReader.
     * <p>
     * This class does not define any feature (yet) and ignores the SAX mandatory feature. Thus, this member is present
     * only to support the mandatory feature setting and retrieval logic defined by SAX.
     * </p>
     */
    private final Map<String, Boolean> features = new HashMap<>();

    /**
     * The SAX properties defined for this XMLReader.
     */
    private final Map<String, Object> properties = new HashMap<>();

    private final boolean includeEnclosingDocument;

    /**
     * @since 1.4
     */
    public SaxWriter(final NameCoder nameCoder) {
        this(true, nameCoder);
    }

    /**
     * @since 1.4
     */
    public SaxWriter(final boolean includeEnclosingDocument, final NameCoder nameCoder) {
        super(nameCoder);
        this.includeEnclosingDocument = includeEnclosingDocument;
    }

    /**
     * @deprecated As of 1.4 use {@link SaxWriter#SaxWriter(NameCoder)} instead.
     */
    @Deprecated
    public SaxWriter(final XmlFriendlyReplacer replacer) {
        this(true, replacer);
    }

    /**
     * @deprecated As of 1.4 use {@link SaxWriter#SaxWriter(boolean, NameCoder)} instead.
     */
    @Deprecated
    public SaxWriter(final boolean includeEnclosingDocument, final XmlFriendlyReplacer replacer) {
        this(includeEnclosingDocument, (NameCoder)replacer);
    }

    public SaxWriter(final boolean includeEnclosingDocument) {
        this(includeEnclosingDocument, new XmlFriendlyNameCoder());
    }

    public SaxWriter() {
        this(true);
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    /**
     * Sets the state of a feature.
     * <p>
     * The feature name is any fully-qualified URI.
     * </p>
     * <p>
     * All XMLReaders are required to support setting <code>http://xml.org/sax/features/namespaces</code> to
     * <code>true</code> and <code>http://xml.org/sax/features/namespace-prefixes</code> to <code>false</code>.
     * </p>
     * <p>
     * Some feature values may be immutable or mutable only in specific contexts, such as before, during, or after a
     * parse.
     * </p>
     * <p>
     * <strong>Note</strong>: This implementation only supports the two mandatory SAX features.
     * </p>
     *
     * @param name the feature name, which is a fully-qualified URI.
     * @param value the requested state of the feature (true or false).
     * @throws SAXNotRecognizedException when the XMLReader does not recognize the feature name.
     * @see #getFeature
     */
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException {
        if (name.equals("http://xml.org/sax/features/namespaces")
            || name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            features.put(name, value ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    /**
     * Looks up the value of a feature.
     * <p>
     * The feature name is any fully-qualified URI. It is possible for an XMLReader to recognize a feature name but to
     * be unable to return its value; this is especially true in the case of an adapter for a SAX1 Parser, which has no
     * way of knowing whether the underlying parser is performing validation or expanding external entities.
     * </p>
     * <p>
     * All XMLReaders are required to recognize the <code>http://xml.org/sax/features/namespaces</code> and the
     * <code>http://xml.org/sax/features/namespace-prefixes</code> feature names.
     * </p>
     * <p>
     * Some feature values may be available only in specific contexts, such as before, during, or after a parse.
     * </p>
     * <p>
     * Implementors are free (and encouraged) to invent their own features, using names built on their own URIs.
     * </p>
     *
     * @param name the feature name, which is a fully-qualified URI.
     * @return the current state of the feature (true or false).
     * @throws SAXNotRecognizedException when the XMLReader does not recognize the feature name.
     * @see #setFeature
     */
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException {
        if (name.equals("http://xml.org/sax/features/namespaces")
            || name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            Boolean value = features.get(name);

            if (value == null) {
                value = Boolean.FALSE;
            }
            return value.booleanValue();
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    /**
     * Sets the value of a property.
     * <p>
     * The property name is any fully-qualified URI. It is possible for an XMLReader to recognize a property name but to
     * be unable to set its value.
     * </p>
     * <p>
     * XMLReaders are not required to recognize setting any specific property names, though a core set is provided with
     * SAX2.
     * </p>
     * <p>
     * Some property values may be immutable or mutable only in specific contexts, such as before, during, or after a
     * parse.
     * </p>
     * <p>
     * This method is also the standard mechanism for setting extended handlers.
     * </p>
     * <p>
     * <strong>Note</strong>: This implementation only supports four (proprietary) properties:
     * {@link #CONFIGURED_XSTREAM_PROPERTY}, {@link #SOURCE_OBJECT_QUEUE_PROPERTY}. {@link #OOS_SUPPLIER_PROPERTY} and
     * the legacy {@link #SOURCE_OBJECT_LIST_PROPERTY}.
     * </p>
     *
     * @param name the property name, which is a fully-qualified URI.
     * @param value the requested value for the property.
     * @throws SAXNotRecognizedException when the XMLReader does not recognize the property name.
     * @throws SAXNotSupportedException when the XMLReader recognizes the property name but cannot set the requested
     *             value.
     * @see #getProperty
     */
    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        switch (name) {
        case CONFIGURED_XSTREAM_PROPERTY:
            if (!(value instanceof XStream)) {
                throw new SAXNotSupportedException(String
                    .format("Value for property \"%s\" must be a non-null XStream object",
                        CONFIGURED_XSTREAM_PROPERTY));
            }
            break;
        case SOURCE_OBJECT_LIST_PROPERTY:
            if (value instanceof List) {
                value = new LinkedList<>((List<?>)value);
                properties.put(name, value); // just for backward compatibility
            } else {
                throw new SAXNotSupportedException(String
                    .format("Value for property \"%s\" must be a non-null List object", name));
            }
            //$FALL-THROUGH$
        case SOURCE_OBJECT_QUEUE_PROPERTY:
            if (value instanceof BlockingQueue) {
                // OK, take it directly
            } else if (value instanceof Queue) {
                final Queue<?> queue = (Queue<?>)value;

                if (queue.isEmpty()) {
                    throw new SAXNotSupportedException(String
                        .format("Value for property \"%s\" shall not be an empty %s", name, name
                            .equals(SOURCE_OBJECT_QUEUE_PROPERTY) ? "queue" : "list"));
                } else if (name.equals(SOURCE_OBJECT_QUEUE_PROPERTY)) {
                    // Perform a copy of the list to prevent the application to
                    // modify its content while the parse is being performed.
                    value = new ArrayDeque<>(queue);
                }
            } else {
                throw new SAXNotSupportedException(String
                    .format("Value for property \"%s\" must be a non-null Queue object", name));
            }
            name = SOURCE_OBJECT_QUEUE_PROPERTY;
            break;
        case OOS_SUPPLIER_PROPERTY:
            if (value instanceof Supplier) {
                // OK
            } else {
                throw new SAXNotSupportedException(String
                    .format("Value for property \"%s\" has to be a supplier for the ObjectOutputStream",
                        OOS_SUPPLIER_PROPERTY));
            }
            break;
        default:
            throw new SAXNotRecognizedException(name);
        }
        properties.put(name, value);
    }

    /**
     * Looks up the value of a property.
     * <p>
     * The property name is any fully-qualified URI. It is possible for an XMLReader to recognize a property name but to
     * be unable to return its state.
     * </p>
     * <p>
     * XMLReaders are not required to recognize any specific property names, though an initial core set is documented
     * for SAX2.
     * </p>
     * <p>
     * Some property values may be available only in specific contexts, such as before, during, or after a parse.
     * </p>
     * <p>
     * Implementors are free (and encouraged) to invent their own properties, using names built on their own URIs.
     * </p>
     *
     * @param name the property name, which is a fully-qualified URI.
     * @return the current value of the property.
     * @throws SAXNotRecognizedException when the XMLReader does not recognize the property name.
     * @see #getProperty
     */
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException {
        switch (name) {
        case CONFIGURED_XSTREAM_PROPERTY:
        case OOS_SUPPLIER_PROPERTY:
        case SOURCE_OBJECT_QUEUE_PROPERTY:
        case SOURCE_OBJECT_LIST_PROPERTY:
            return properties.get(name);
        default:
            throw new SAXNotRecognizedException(name);
        }
    }

    // ---------------------------------------------------------------------
    // Event handlers
    // ---------------------------------------------------------------------

    /**
     * Allows an application to register an entity resolver.
     * <p>
     * If the application does not register an entity resolver, the XMLReader will perform its own default resolution.
     * </p>
     * <p>
     * Applications may register a new or different resolver in the middle of a parse, and the SAX parser must begin
     * using the new resolver immediately.
     * </p>
     *
     * @param resolver the entity resolver.
     * @throws NullPointerException if the resolver argument is <code>null</code>.
     * @see #getEntityResolver
     */
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException("resolver");
        }
        entityResolver = resolver;
        return;
    }

    /**
     * Returns the current entity resolver.
     *
     * @return the current entity resolver, or <code>null</code> if none has been registered.
     * @see #setEntityResolver
     */
    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    /**
     * Allows an application to register a DTD event handler.
     * <p>
     * If the application does not register a DTD handler, all DTD events reported by the SAX parser will be silently
     * ignored.
     * </p>
     * <p>
     * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
     * using the new handler immediately.
     * </p>
     *
     * @param handler the DTD handler.
     * @throws NullPointerException if the handler argument is <code>null</code>.
     * @see #getDTDHandler
     */
    @Override
    public void setDTDHandler(final DTDHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        dtdHandler = handler;
        return;
    }

    /**
     * Returns the current DTD handler.
     *
     * @return the current DTD handler, or <code>null</code> if none has been registered.
     * @see #setDTDHandler
     */
    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    /**
     * Allows an application to register a content event handler.
     * <p>
     * If the application does not register a content handler, all content events reported by the SAX parser will be
     * silently ignored.
     * </p>
     * <p>
     * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
     * using the new handler immediately.
     * </p>
     *
     * @param handler the content handler.
     * @throws NullPointerException if the handler argument is <code>null</code>.
     * @see #getContentHandler
     */
    @Override
    public void setContentHandler(final ContentHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        contentHandler = handler;
        return;
    }

    /**
     * Returns the current content handler.
     *
     * @return the current content handler, or <code>null</code> if none has been registered.
     * @see #setContentHandler
     */
    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    /**
     * Allows an application to register an error event handler.
     * <p>
     * If the application does not register an error handler, all error events reported by the SAX parser will be
     * silently ignored; however, normal processing may not continue. It is highly recommended that all SAX applications
     * implement an error handler to avoid unexpected bugs.
     * </p>
     * <p>
     * Applications may register a new or different handler in the middle of a parse, and the SAX parser must begin
     * using the new handler immediately.
     * </p>
     *
     * @param handler the error handler.
     * @throws NullPointerException if the handler argument is <code>null</code>.
     * @see #getErrorHandler
     */
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        errorHandler = handler;
        return;
    }

    /**
     * Returns the current error handler.
     *
     * @return the current error handler, or <code>null</code> if none has been registered.
     * @see #setErrorHandler
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    // ---------------------------------------------------------------------
    // Parsing
    // ---------------------------------------------------------------------

    /**
     * Parses an XML document from a system identifier (URI).
     * <p>
     * This method is a shortcut for the common case of reading a document from a system identifier. It is the exact
     * equivalent of the following:
     * </p>
     * <blockquote>
     *
     * <pre>
     * parse(new InputSource(systemId));
     * </pre>
     *
     * </blockquote>
     * <p>
     * If the system identifier is a URL, it must be fully resolved by the application before it is passed to the
     * parser.
     * </p>
     * <p>
     * <strong>Note</strong>: As a custom SAX parser, this class ignores the <code>systemId</code> argument of this
     * method and relies on the proprietary SAX property {@link #SOURCE_OBJECT_QUEUE_PROPERTY}) to define the list of
     * objects to serialize.
     * </p>
     *
     * @param systemId the system identifier (URI).
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see #parse(org.xml.sax.InputSource)
     */
    @Override
    public void parse(final String systemId) throws SAXException {
        this.parse();
    }

    /**
     * Parse an XML document.
     * <p>
     * The application can use this method to instruct the XML reader to begin parsing an XML document from any valid
     * input source (a character stream, a byte stream, or a URI).
     * </p>
     * <p>
     * Applications may not invoke this method while a parse is in progress (they should create a new XMLReader instead
     * for each nested XML document). Once a parse is complete, an application may reuse the same XMLReader object,
     * possibly with a different input source.
     * </p>
     * <p>
     * During the parse, the XMLReader will provide information about the XML document through the registered event
     * handlers.
     * </p>
     * <p>
     * This method is synchronous: it will not return until parsing has ended. If a client application wants to
     * terminate parsing early, it should throw an exception.
     * </p>
     * <p>
     * <strong>Note</strong>: As a custom SAX parser, this class ignores the <code>source</code> argument of this method
     * and relies on the proprietary SAX property {@link #SOURCE_OBJECT_QUEUE_PROPERTY}) to define the list of objects
     * to serialize.
     * </p>
     *
     * @param input The input source for the top-level of the XML document.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setContentHandler
     * @see #setErrorHandler
     */
    @Override
    public void parse(final InputSource input) throws SAXException {
        this.parse();
    }

    /**
     * Serializes the Java objects of the configured list into a flow of SAX events.
     *
     * @throws SAXException if the configured object list is invalid or object serialization failed.
     */
    private void parse() throws SAXException {
        XStream xstream = (XStream)properties.get(CONFIGURED_XSTREAM_PROPERTY);
        if (xstream == null) {
            xstream = new XStream();
        }

        final Queue<?> source = (Queue<?>)properties.get(SOURCE_OBJECT_QUEUE_PROPERTY);
        if (source == null || source.isEmpty() && !(source instanceof BlockingQueue)) {
            throw new SAXException("Missing or empty source object queue. Setting property \""
                + SOURCE_OBJECT_QUEUE_PROPERTY
                + "\" is mandatory");
        }

        try {
            @SuppressWarnings("unchecked")
            final Supplier<ObjectOutputStream> supplier = (Supplier<ObjectOutputStream>)properties
                .get(OOS_SUPPLIER_PROPERTY);
            if (supplier != null) {
                final ObjectOutputStream oos = supplier.get();
                for (Object o = null; o != EOS;) {
                    o = source instanceof BlockingQueue ? ((BlockingQueue<?>)source).take() : source.poll();
                    if (o != EOS) {
                        oos.writeObject(o);
                    }
                }
                oos.close();
            } else {
                startDocument(true);
                for (final Object name : source) {
                    xstream.marshal(name, this);
                }
                endDocument(true);
            }
        } catch (final IOException | InterruptedException e) {
            if (e.getCause() instanceof SAXException) {
                throw (SAXException)e.getCause();
            } else {
                throw new SAXException(e);
            }
        }
    }

    // =========================================================================
    // XStream HierarchicalStreamWriter interface support
    // =========================================================================

    private int depth = 0;
    private final List<String> elementStack = new LinkedList<>();
    private char[] buffer = new char[128];
    private boolean startTagInProgress = false;
    private final AttributesImpl attributeList = new AttributesImpl();

    @Override
    public void startNode(final String name) {
        try {
            if (depth != 0) {
                flushStartTag();
            } else if (includeEnclosingDocument) {
                startDocument(false);
            }
            elementStack.add(0, escapeXmlName(name));

            startTagInProgress = true;
            depth++;
        } catch (final SAXException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void addAttribute(final String name, final String value) {
        if (startTagInProgress) {
            final String escapedName = escapeXmlName(name);
            attributeList.addAttribute("", escapedName, escapedName, "CDATA", value);
        } else {
            throw new StreamException(new IllegalStateException("No startElement being processed"));
        }
    }

    @Override
    public void setValue(final String text) {
        try {
            flushStartTag();

            final int lg = text.length();
            if (lg > buffer.length) {
                buffer = new char[lg];
            }
            text.getChars(0, lg, buffer, 0);

            contentHandler.characters(buffer, 0, lg);
        } catch (final SAXException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void endNode() {
        try {
            flushStartTag();

            final String tagName = elementStack.remove(0);

            contentHandler.endElement("", tagName, tagName);

            depth--;
            if (depth == 0 && includeEnclosingDocument) {
                endDocument(false);
            }
        } catch (final SAXException e) {
            throw new StreamException(e);
        }
    }

    /**
     * Fires the SAX startDocument event towards the configured ContentHandler.
     *
     * @param multiObjectMode whether serialization of several object will be merge into a single SAX document.
     * @throws SAXException if thrown by the ContentHandler.
     */
    private void startDocument(final boolean multiObjectMode) throws SAXException {
        if (depth == 0) {
            // Notify contentHandler of document start.
            contentHandler.startDocument();

            if (multiObjectMode) {
                // Prevent marshalling of each object to fire its own
                // start/endDocument events.
                depth++;
            }
        }
    }

    /**
     * Fires the SAX endDocument event towards the configured ContentHandler.
     *
     * @param multiObjectMode whether serialization of several object will be merge into a single SAX document.
     * @throws SAXException if thrown by the ContentHandler.
     */
    private void endDocument(final boolean multiObjectMode) throws SAXException {
        if (depth == 0 || depth == 1 && multiObjectMode) {
            contentHandler.endDocument();
            depth = 0;
        }
    }

    /**
     * Fires any pending SAX startElement event towards the configured ContentHandler.
     *
     * @throws SAXException if thrown by the ContentHandler.
     */
    private void flushStartTag() throws SAXException {
        if (startTagInProgress) {
            final String tagName = elementStack.get(0);

            contentHandler.startElement("", tagName, tagName, attributeList);
            attributeList.clear();
            startTagInProgress = false;
        }
    }

    @Override
    public void flush() {
        // don't need to do anything
    }

    @Override
    public void close() {
        // don't need to do anything
    }
}
