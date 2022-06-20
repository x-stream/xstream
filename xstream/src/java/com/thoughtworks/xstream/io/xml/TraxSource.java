/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2015, 2020, 2022 XStream Committers.
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
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.ListWrappingQueue;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A {@link SAXSource JAXP TrAX Source} that enables using XStream object serialization as direct input for XSLT
 * processors without resorting to an intermediate representation such as text XML, DOM or DOM4J.
 * <p>
 * The following example shows how to apply an XSL Transformation to a set of Java objects gathered into a List (
 * <code>source</code>):
 * </p>
 *
 * <pre>
 * <code>
 * public static String transform(List source, String stylesheet) {
 *     try {
 *         Transformer transformer = TransformerFactory.newInstance().newTransformer(
 *             new StreamSource(stylesheet));
 *         TraxSource in = new TraxSource(source);
 *         Writer out = new StringWriter();
 *         transformer.transform(in, new StreamResult(out));
 *         return out.toString();
 *     } catch (TransformerException e) {
 *         throw new RuntimeException(&quot;XSLT Transformation failed&quot;, e);
 *     }
 * }
 * </code>
 * </pre>
 *
 * @author Laurent Bihanic
 * @author Joerg Schaible
 */
public class TraxSource extends SAXSource {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature} returns <code>true</code> when passed this value as
     * an argument, the Transformer natively supports XStream.
     * <p>
     * <strong>Note</strong>: This implementation does not override the {@link SAXSource#FEATURE} value defined by its
     * superclass to be considered as a SAXSource by Transformer implementations not natively supporting this
     * XStream-specific source
     * </p>
     */
    public final static String XSTREAM_FEATURE = "http://com.thoughtworks.xstream/XStreamSource/feature";
    private final static Mapper.Null NULL = new Mapper.Null();

    /**
     * The XMLReader object associated to this source or <code>null</code> if no XMLReader has yet been requested.
     *
     * @see #getXMLReader
     */
    private XMLReader xmlReader = null;
    private SaxWriter saxWriter = null;

    /**
     * The configured XStream facade to use for serializing objects.
     */
    private XStream xstream = null;

    /**
     * The queue of Java objects to be serialized.
     */
    private Queue<?> source = null;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a XStream TrAX source.
     */
    public TraxSource() {
        super(new InputSource());
    }

    /**
     * Creates a XStream TrAX source, specifying the object to marshal.
     *
     * @param source the object to marshal.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     * @see #setSource(java.lang.Object)
     */
    public TraxSource(final Object source) {
        super(new InputSource());

        setSource(source);
    }

    /**
     * Creates a XStream TrAX source, specifying the object to marshal and a configured (with aliases) XStream facade.
     *
     * @param source the object to marshal.
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>source</code> or <code>xstream</code> is <code>null</code>.
     * @see #setSource(java.lang.Object)
     * @see #setXStream(com.thoughtworks.xstream.XStream)
     */
    public TraxSource(final Object source, final XStream xstream) {
        super(new InputSource());

        setSource(source);
        setXStream(xstream);
    }

    /**
     * Creates a XStream TrAX source, setting the objects to marshal.
     *
     * @param source the list of objects to marshal.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code> or empty.
     * @see #setSourceAsList(java.util.List)
     */
    public TraxSource(final List<?> source) {
        super(new InputSource());

        setSourceAsList(source);
    }

    /**
     * Creates a XStream TrAX source, setting the objects to marshal and a configured (with aliases) XStream facade.
     *
     * @param source the list of objects to marshal.
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>source</code> or <code>xstream</code> is <code>null</code> or
     *             <code>source</code> is empty.
     * @see #setSourceAsList(java.util.List)
     * @see #setXStream(com.thoughtworks.xstream.XStream)
     */
    public TraxSource(final List<?> source, final XStream xstream) {
        super(new InputSource());

        setSourceAsList(source);
        setXStream(xstream);
    }

    // -------------------------------------------------------------------------
    // SAXSource overwritten methods
    // -------------------------------------------------------------------------

    /**
     * Sets the SAX InputSource to be used for the Source.
     * <p>
     * As this implementation only supports object lists as data source, this method always throws an
     * {@link UnsupportedOperationException}.
     * </p>
     *
     * @param inputSource a valid InputSource reference.
     * @throws UnsupportedOperationException always!
     */
    @Override
    public void setInputSource(final InputSource inputSource) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the XMLReader to be used for the Source.
     * <p>
     * As this implementation only supports object lists as data source, this method throws an
     * {@link UnsupportedOperationException} if the provided reader object does not implement the SAX {@link XMLFilter}
     * interface. Otherwise, a {@link SaxWriter} instance will be attached as parent of the filter chain.
     * </p>
     *
     * @param reader a valid XMLReader or XMLFilter reference.
     * @throws UnsupportedOperationException if <code>reader</code> is not a SAX {@link XMLFilter}.
     * @see #getXMLReader
     */
    @Override
    public void setXMLReader(final XMLReader reader) {
        createXMLReader(reader);
    }

    /**
     * Returns the XMLReader to be used for the Source.
     * <p>
     * This implementation returns a specific XMLReader ({@link SaxWriter}) generating the XML from a list of input
     * objects.
     * </p>
     *
     * @return an XMLReader generating the XML from a list of input objects.
     */
    @Override
    public XMLReader getXMLReader() {
        if (xmlReader == null) {
            createXMLReader(null);
        }
        return xmlReader;
    }

    // -------------------------------------------------------------------------
    // Specific implementation
    // -------------------------------------------------------------------------

    /**
     * Sets the XStream facade to use when marshalling objects.
     *
     * @param xstream a configured XStream facade.
     * @throws IllegalArgumentException if <code>xstream</code> is <code>null</code>.
     */
    public void setXStream(final XStream xstream) {
        if (xstream == null) {
            throw new IllegalArgumentException("xstream");
        }
        this.xstream = xstream;

        configureXMLReader();
    }

    /**
     * Sets the object to marshal.
     *
     * @param obj the object to marshal.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>.
     */
    public void setSource(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj");
        }
        final List<Object> list = new LinkedList<>();
        list.add(obj);

        setSourceAsList(list);
    }

    /**
     * Sets the list of objects to marshal.
     * <p>
     * When dealing with non-text input (such as SAX or DOM), XSLT processors support multiple root node children for
     * the source tree (see <a href="http://www.w3.org/TR/xslt#root-node-children">section 3.1</a> of the &quot;XSL
     * Transformations (XSLT) Version 1.0&quot; specification. Using a list of objects as source makes use of this
     * feature and allows creating XML documents merging the XML serialization of several Java objects.
     *
     * @param list the list of objects to marshal.
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code> or empty.
     */
    public void setSourceAsList(final List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list");
        }
        @SuppressWarnings("unchecked")
        final List<Object> olist = (List<Object>)list;
        source = list instanceof Queue ? (Queue<?>)list : new ListWrappingQueue<>(olist);

        configureXMLReader();
    }

    private void createXMLReader(final XMLReader filterChain) {
        if (filterChain == null) {
            xmlReader = saxWriter = new SaxWriter();
        } else {
            if (filterChain instanceof XMLFilter) {
                // Connect the filter chain to a document reader.
                XMLFilter filter = (XMLFilter)filterChain;
                while (filter.getParent() instanceof XMLFilter) {
                    filter = (XMLFilter)filter.getParent();
                }
                if (!(filter.getParent() instanceof SaxWriter)) {
                    saxWriter = new SaxWriter();
                    filter.setParent(saxWriter);
                }

                // Read XML data from filter chain.
                xmlReader = filterChain;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        configureXMLReader();
    }

    private void configureXMLReader() {
        if (xmlReader != null) {
            try {
                if (xstream != null) {
                    xmlReader.setProperty(SaxWriter.CONFIGURED_XSTREAM_PROPERTY, xstream);
                }
                if (source != null) {
                    xmlReader.setProperty(SaxWriter.SOURCE_OBJECT_QUEUE_PROPERTY, source);
                }
            } catch (final SAXException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the XSL template
     * @param target the target stream
     * @throws TransformerConfigurationException if the style sheet is invalid
     * @throws IOException if writing into the output stream fails
     * @see #createObjectOutputStream(Templates, Writer, String)
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Source stylesheet,  final OutputStream target)
            throws IOException, TransformerConfigurationException {
        return createObjectOutputStream(stylesheet, new OutputStreamWriter(target, StandardCharsets.UTF_8));
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the XSL template
     * @param target the target stream
     * @throws IOException if writing into the output stream fails
     * @see #createObjectOutputStream(Templates, Writer, String)
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Templates stylesheet, final OutputStream target)
            throws IOException {
        return createObjectOutputStream(stylesheet, new OutputStreamWriter(target, StandardCharsets.UTF_8));
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the source with the XSL
     * @param writer the target writer
     * @throws TransformerConfigurationException if the style sheet is invalid
     * @throws IOException if writing into the writer fails
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Source stylesheet, final Writer writer)
            throws IOException, TransformerConfigurationException {
        return createObjectOutputStream(stylesheet, writer, "object-stream");
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the XSL template
     * @param writer the target writer
     * @throws IOException if writing into the writer fails
     * @see #createObjectOutputStream(Templates, Writer, String)
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Templates stylesheet, final Writer writer)
            throws IOException {
        return createObjectOutputStream(stylesheet, writer, "object-stream");
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the source with the XSL
     * @param target the target stream
     * @param rootNodeName the name of the root element
     * @throws TransformerConfigurationException if the style sheet is invalid
     * @throws IOException if writing into the output stream fails
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Source stylesheet, final OutputStream target,
            final String rootNodeName)
            throws IOException, TransformerConfigurationException {
        return createObjectOutputStream(stylesheet, new OutputStreamWriter(target, StandardCharsets.UTF_8),
            rootNodeName);
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the XSL template
     * @param target the target stream
     * @param rootNodeName the name of the root element
     * @throws IOException if writing into the output stream fails
     * @see #createObjectOutputStream(Templates, OutputStream, String)
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Templates stylesheet, final OutputStream target,
            final String rootNodeName)
            throws IOException {
        return createObjectOutputStream(stylesheet, new OutputStreamWriter(target, StandardCharsets.UTF_8),
            rootNodeName);
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the source with the XSL
     * @param writer the target writer
     * @param rootNodeName the name of the root element
     * @throws TransformerConfigurationException if the style sheet is invalid
     * @throws IOException if writing into the writer fails
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Source stylesheet, final Writer writer,
            final String rootNodeName)
            throws IOException, TransformerConfigurationException {
        final Templates templates = TransformerFactory.newInstance().newTemplates(stylesheet);
        return createObjectOutputStream(templates, writer, rootNodeName);
    }

    /**
     * Create a transforming ObjectOutputStream.
     * <p>
     * Note, the stream instance may throw important exceptions when closed.
     * </p>
     *
     * @param stylesheet the XSL template
     * @param writer the target writer
     * @param rootNodeName the name of the root element
     * @throws IOException if writing into the writer fails
     * @see XStream#createObjectOutputStream(com.thoughtworks.xstream.io.HierarchicalStreamWriter, String)
     * @since upcoming
     */
    public ObjectOutputStream createObjectOutputStream(final Templates stylesheet, final Writer writer,
            final String rootNodeName)
            throws IOException {
        getXMLReader();
        final CompletableFuture<CustomObjectOutputStream> future = new CompletableFuture<>();
        final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
        final Supplier<CustomObjectOutputStream> supplier = () -> {
            try {
                @SuppressWarnings("resource")
                final CustomObjectOutputStream customObjectOutputStream = (CustomObjectOutputStream)xstream
                    .createObjectOutputStream(saxWriter, rootNodeName);
                future.complete(customObjectOutputStream);
                return customObjectOutputStream;
            } catch (final IOException e) {
                throw new StreamException(e);
            }
        };
        try {
            xmlReader.setProperty(SaxWriter.OOS_SUPPLIER_PROPERTY, supplier);
            xmlReader.setProperty(SaxWriter.SOURCE_OBJECT_QUEUE_PROPERTY, queue);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        final TransformerException transEx[] = new TransformerException[1];
        final Thread t = new Thread(() -> {
            Transformer transformer;
            try {
                transformer = stylesheet.newTransformer();
                transformer.transform(this, new StreamResult(writer));
            } catch (final TransformerException e) {
                transEx[0] = e;
            }
        });
        t.start();
        t.setName("XStream OutputObjectStream Transformer");
        try {
            final CustomObjectOutputStream customObjectOutputStream = future.get();
            final CustomObjectOutputStream.StreamCallback callback = customObjectOutputStream.popCallback();
            customObjectOutputStream.pushCallback(new CustomObjectOutputStream.StreamCallback() {

                @Override
                public void writeToStream(final Object object) throws IOException {
                    if (Thread.currentThread() == t) {
                        callback.writeToStream(object == NULL ? null : object);
                    } else {
                        TraxSource.checkException(transEx[0]);
                        try {
                            queue.put(object == null ? NULL : object);
                        } catch (final InterruptedException e) {
                            throw new InterruptedIOException();
                        }
                    }
                }

                @Override
                public void writeFieldsToStream(final Map<String, Object> fields) throws IOException {
                    callback.writeFieldsToStream(fields);
                }

                @Override
                public void defaultWriteObject() throws IOException {
                    callback.defaultWriteObject();
                }

                @Override
                public void flush() throws IOException {
                    callback.flush();
                }

                @Override
                public void close() throws IOException {
                    if (Thread.currentThread() == t) {
                        callback.close();
                    } else {
                        TraxSource.checkException(transEx[0]);
                        writeToStream(SaxWriter.EOS);
                        try {
                            t.join();
                        } catch (final InterruptedException e) {
                            throw new InterruptedIOException();
                        }
                    }
                }

            });
            checkException(transEx[0]);
            return customObjectOutputStream;
        } catch (InterruptedException | ExecutionException e) {
            throw new StreamException("Failed to communicate with transformation thread", e);
        }
    }

    private static void checkException(final TransformerException transformerException) throws Error, IOException {
        if (transformerException != null) {
            final Throwable e = transformerException.getCause();
            if (e instanceof Error) {
                throw (Error)e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw new StreamException("XSLT transformation failed", transformerException);
        }
    }
}
