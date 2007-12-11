/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Self-contained XStream generator. The class is a utility to write XML streams that contain 
 * additionally the XStream that was used to serialize the object graph. Such a stream can
 * be unmarshalled using this embedded XStream instance, that kept any settings.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class XStreamer {

    /**
     * Serialize an object including the XStream to a pretty-printed XML String.
     * 
     * @throws ObjectStreamException if the XML contains non-serializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be serialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public String toXML(XStream xstream, Object obj) throws ObjectStreamException {
        Writer writer = new StringWriter();
        try {
            toXML(xstream, obj, writer);
        } catch (ObjectStreamException e) {
            throw e;
        } catch (IOException e) {
            throw new ConversionException("Unexpeced IO error from a StringWriter", e);
        }
        return writer.toString();
    }

    /**
     * Serialize an object including the XStream to the given Writer as pretty-printed XML.
     * <p>
     * Warning: XStream will serialize itself into this XML stream. To read such an XML code, you
     * should use {@link XStreamer#fromXML(Reader)} or one of the other overloaded
     * methods. Since a lot of internals are written into the stream, you cannot expect to use such
     * an XML to work with another XStream version or with XStream running on different JDKs and/or
     * versions. We have currently no JDK 1.3 support, nor will the PureReflectionConverter work
     * with a JDK less than 1.5.
     * </p>
     * 
     * @throws IOException if an error occurs reading from the Writer.
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be serialized
     * @since 1.2
     */
    public void toXML(XStream xstream, Object obj, Writer out)
            throws IOException {
        XStream outer = new XStream();
        ObjectOutputStream oos = outer.createObjectOutputStream(out);
        try {
            oos.writeObject(xstream);
            oos.flush();
            xstream.toXML(obj, out);
        } finally {
            oos.close();
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String. The method will use
     * internally an XppDriver to load the contained XStream instance.
     * 
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public Object fromXML(String xml) throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(new StringReader(xml));
        } catch (ObjectStreamException e) {
            throw e;
        } catch (IOException e) {
            throw new ConversionException("Unexpeced IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from a String.
     * 
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws ObjectStreamException if the XML contains non-deserializable elements
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public Object fromXML(HierarchicalStreamDriver driver, String xml)
            throws ClassNotFoundException, ObjectStreamException {
        try {
            return fromXML(driver, new StringReader(xml));
        } catch (ObjectStreamException e) {
            throw e;
        } catch (IOException e) {
            throw new ConversionException("Unexpeced IO error from a StringReader", e);
        }
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader. The method will use
     * internally an XppDriver to load the contained XStream instance.
     * 
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     * @see #toXML(XStream, Object, Writer)
     */
    public Object fromXML(Reader xml)
            throws IOException, ClassNotFoundException {
        return fromXML(new XppDriver(), xml);
    }

    /**
     * Deserialize a self-contained XStream with object from an XML Reader.
     * 
     * @throws IOException if an error occurs reading from the Reader.
     * @throws ClassNotFoundException if a class in the XML stream cannot be found
     * @throws com.thoughtworks.xstream.XStreamException if the object cannot be deserialized
     * @since 1.2
     */
    public Object fromXML(HierarchicalStreamDriver driver, Reader xml)
            throws IOException, ClassNotFoundException {
        XStream outer = new XStream(driver);
        HierarchicalStreamReader reader = driver.createReader(xml);
        ObjectInputStream configIn = outer.createObjectInputStream(reader);
        try {
            XStream configured = (XStream)configIn.readObject();
            ObjectInputStream in = configured.createObjectInputStream(reader);
            try {
                return in.readObject();
            } finally {
                in.close();
            }
        } finally {
            configIn.close();
        }
    }

}
