/*
 * Copyright (C) 2006, 2007, 2014, 2016, 2019, 2020, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.datatype.DatatypeFactory;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanProvider;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import com.thoughtworks.xstream.security.TypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;


/**
 * Self-contained XStream generator. The class is a utility to write XML streams that contain additionally the XStream
 * that was used to serialize the object graph. Such a stream can be unmarshalled using this embedded XStream instance,
 * that kept any settings.
 *
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class XStreamer {

    private final static TypePermission[] PERMISSIONS = {
        new TypeHierarchyPermission(ConverterMatcher.class), //
        new TypeHierarchyPermission(Mapper.class), //
        new TypeHierarchyPermission(XStream.class), //
        new TypeHierarchyPermission(ReflectionProvider.class), //
        new TypeHierarchyPermission(JavaBeanProvider.class), //
        new TypeHierarchyPermission(FieldKeySorter.class), //
        new TypeHierarchyPermission(ConverterLookup.class), //
        new TypeHierarchyPermission(ConverterRegistry.class), //
        new TypeHierarchyPermission(HierarchicalStreamDriver.class), //
        new TypeHierarchyPermission(MarshallingStrategy.class), //
        new TypeHierarchyPermission(MarshallingContext.class), //
        new TypeHierarchyPermission(UnmarshallingContext.class), //
        new TypeHierarchyPermission(NameCoder.class), //
        new TypeHierarchyPermission(TypePermission.class), //
        new WildcardTypePermission(true, JVM.class.getPackage().getName() + ".**"), //
        new TypeHierarchyPermission(DatatypeFactory.class) // required by DurationConverter
    };

    /**
     * Serialize an object including the XStream to a pretty-printed XML String.
     *
     * @throws ObjectStreamException if the XML contains non-serializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be serialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public String toXML(final XStream xstream, final Object obj) throws ObjectStreamException {
        final Writer writer = new StringWriter();
        try {
            toXML(xstream, obj, writer);
        } catch (final ObjectStreamException e) {
            throw e;
        } catch (final IOException e) {
            throw new StreamException("Unexpected IO error from a StringWriter", e);
        }
        return writer.toString();
    }

    /**
     * Serialize an object including the XStream to the given Writer as pretty-printed XML.
     * <p>
     * Warning: XStream will serialize itself into this XML stream. To read such an XML code, you should use
     * {@link XStreamer#fromXML(Reader)} or one of the other overloaded methods. Since a lot of internals are written
     * into the stream, you cannot expect to use such an XML to work with another XStream version or with XStream
     * running on different JDKs and/or versions. We have currently no JDK 1.3 support, nor will the
     * PureReflectionConverter work with a JDK less than 1.5.
     * </p>
     *
     * @throws IOException if an error occurs reading from the Writer.
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be serialized
     * @since 1.2
     */
    public void toXML(final XStream xstream, final Object obj, final Writer out) throws IOException {
        final XStream outer = new XStream();
        try (final ObjectOutputStream oos = outer.createObjectOutputStream(out)) {
            oos.writeObject(xstream);
            oos.flush();
            xstream.toXML(obj, out);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String. The method will use internally an XppDriver to
     * load the contained XStream instance with default permissions.
     *
     * @param xml the XML data
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final String xml) throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(new StringReader(xml));
        } catch (final ObjectStreamException e) {
            throw e;
        } catch (final IOException e) {
            throw new StreamException("Unexpected IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String. The method will use internally an XppDriver to
     * load the contained XStream instance.
     *
     * @param xml the XML data
     * @param permissions the permissions to use (ensure that they include the defaults)
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.4.7
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final String xml, final TypePermission... permissions)
            throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(new StringReader(xml), permissions);
        } catch (final ObjectStreamException e) {
            throw e;
        } catch (final IOException e) {
            throw new StreamException("Unexpected IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String.
     *
     * @param driver the implementation to use
     * @param xml the XML data
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final HierarchicalStreamDriver driver, final String xml)
            throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(driver, new StringReader(xml));
        } catch (final ObjectStreamException e) {
            throw e;
        } catch (final IOException e) {
            throw new StreamException("Unexpected IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String.
     *
     * @param driver the implementation to use
     * @param xml the XML data
     * @param permissions the permissions to use (ensure that they include the defaults)
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.4.7
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final HierarchicalStreamDriver driver, final String xml, final TypePermission... permissions)
            throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(driver, new StringReader(xml), permissions);
        } catch (final ObjectStreamException e) {
            throw e;
        } catch (final IOException e) {
            throw new StreamException("Unexpected IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader. The method will use internally an XppDriver
     * to load the contained XStream instance with default permissions.
     *
     * @param xml the {@link Reader} providing the XML data
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final Reader xml) throws IOException, ClassNotFoundException {
        return fromXML(DefaultDriver.create(), xml);
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader. The method will use internally an XppDriver
     * to load the contained XStream instance.
     *
     * @param xml the {@link Reader} providing the XML data
     * @param permissions the permissions to use (ensure that they include the defaults)
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.4.7
     * @see #toXML(XStream, Object, Writer)
     */
    public <T> T fromXML(final Reader xml, final TypePermission... permissions)
            throws IOException, ClassNotFoundException {
        return fromXML(DefaultDriver.create(), xml, permissions);
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader.
     *
     * @param driver the implementation to use
     * @param xml the {@link Reader} providing the XML data
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     */
    public <T> T fromXML(final HierarchicalStreamDriver driver, final Reader xml)
            throws IOException, ClassNotFoundException {
        return fromXML(driver, xml, PERMISSIONS);
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader.
     *
     * @param driver the implementation to use
     * @param xml the {@link Reader} providing the XML data
     * @param permissions the permissions to use (ensure that they include the defaults)
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.4.7
     */
    public <T> T fromXML(final HierarchicalStreamDriver driver, final Reader xml, final TypePermission... permissions)
            throws IOException, ClassNotFoundException {
        final XStream outer = new XStream(driver);
        for (final TypePermission permission : permissions) {
            outer.addPermission(permission);
        }
        final HierarchicalStreamReader reader = driver.createReader(xml);
        try (ObjectInputStream configIn = outer.createObjectInputStream(reader)) {
            final XStream configured = (XStream)configIn.readObject();
            final ObjectInputStream in = configured.createObjectInputStream(reader);
            try {
                @SuppressWarnings("unchecked")
                final T t = (T)in.readObject();
                return t;
            } finally {
                in.close();
            }
        }
    }

    /**
     * Retrieve the default permissions to unmarshal an XStream instance.
     * <p>
     * The returned list will only cover permissions for XStream's own types. If your custom converters or mappers keep
     * references to other types, you will have to add permission for those types on your own.
     * </p>
     *
     * @since 1.4.7
     */
    public static TypePermission[] getDefaultPermissions() {
        return PERMISSIONS.clone();
    }
}
