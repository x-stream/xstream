/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2013, 2014, 2015, 2016, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts any object that implements the {@link Externalizable} interface, allowing compatibility with native Java
 * serialization.
 *
 * @author Joe Walnes
 */
public class ExternalizableConverter implements Converter {

    private final Mapper mapper;
    private final ClassLoaderReference classLoaderReference;
    private transient SerializationMembers serializationMembers;

    /**
     * Construct an ExternalizableConverter.
     *
     * @param mapper the Mapper chain
     * @param classLoaderReference the reference to XStream's {@link ClassLoader} instance
     * @since 1.4.5
     */
    public ExternalizableConverter(final Mapper mapper, final ClassLoaderReference classLoaderReference) {
        this.mapper = mapper;
        this.classLoaderReference = classLoaderReference;
        serializationMembers = new SerializationMembers();
    }

    /**
     * @deprecated As of 1.4.5 use {@link #ExternalizableConverter(Mapper, ClassLoaderReference)}
     */
    @Deprecated
    public ExternalizableConverter(final Mapper mapper, final ClassLoader classLoader) {
        this(mapper, new ClassLoaderReference(classLoader));
    }

    /**
     * @deprecated As of 1.4 use {@link #ExternalizableConverter(Mapper, ClassLoader)}
     */
    @Deprecated
    public ExternalizableConverter(final Mapper mapper) {
        this(mapper, ExternalizableConverter.class.getClassLoader());
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && JVM.canCreateDerivedObjectOutputStream() && Externalizable.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(final Object original, final HierarchicalStreamWriter writer,
            final MarshallingContext context) {
        final Object source = serializationMembers.callWriteReplace(original);
        if (source != original && context instanceof ReferencingMarshallingContext) {
            ((ReferencingMarshallingContext<?>)context).replace(original, source);
        }
        if (source.getClass() != original.getClass()) {
            final String attributeName = mapper.aliasForSystemAttribute("resolves-to");
            if (attributeName != null) {
                writer.addAttribute(attributeName, mapper.serializedClass(source.getClass()));
            }
            context.convertAnother(source);
        } else {
            try {
                final Externalizable externalizable = (Externalizable)source;
                final CustomObjectOutputStream.StreamCallback callback = new CustomObjectOutputStream.StreamCallback() {
                    @Override
                    public void writeToStream(final Object object) {
                        if (object == null) {
                            writer.startNode("null");
                            writer.endNode();
                        } else {
                            writer.startNode(mapper.serializedClass(object.getClass()), object.getClass());
                            context.convertAnother(object);
                            writer.endNode();
                        }
                    }

                    @Override
                    public void writeFieldsToStream(final Map<String, Object> fields) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void defaultWriteObject() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void flush() {
                        writer.flush();
                    }

                    @Override
                    public void close() {
                        throw new UnsupportedOperationException(
                            "Objects are not allowed to call ObjectOutput.close() from writeExternal()");
                    }
                };
                @SuppressWarnings("resource")
                final CustomObjectOutputStream objectOutput = CustomObjectOutputStream.getInstance(context, callback);
                externalizable.writeExternal(objectOutput);
                objectOutput.popCallback();
            } catch (final IOException e) {
                throw new StreamException("Cannot serialize " + source.getClass().getName() + " using Externalization",
                    e);
            }
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Class<?> type = context.getRequiredType();
        final Constructor<?> defaultConstructor;
        try {
            defaultConstructor = type.getDeclaredConstructor();
            if (!defaultConstructor.isAccessible()) {
                defaultConstructor.setAccessible(true);
            }
            final Externalizable externalizable = (Externalizable)defaultConstructor.newInstance();
            final CustomObjectInputStream.StreamCallback callback = new CustomObjectInputStream.StreamCallback() {
                @Override
                public Object readFromStream() {
                    reader.moveDown();
                    final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
                    final Object streamItem = context.convertAnother(externalizable, type);
                    reader.moveUp();
                    return streamItem;
                }

                @Override
                public Map<String, Object> readFieldsFromStream() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void defaultReadObject() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void registerValidation(final ObjectInputValidation validation, final int priority)
                        throws NotActiveException {
                    throw new NotActiveException("stream inactive");
                }

                @Override
                public void close() {
                    throw new UnsupportedOperationException(
                        "Objects are not allowed to call ObjectInput.close() from readExternal()");
                }
            };
            {
                @SuppressWarnings("resource")
                final CustomObjectInputStream objectInput = CustomObjectInputStream.getInstance(context, callback,
                    classLoaderReference);
                externalizable.readExternal(objectInput);
                objectInput.popCallback();
            }
            return serializationMembers.callReadResolve(externalizable);
        } catch (final NoSuchMethodException e) {
            throw new ConversionException("Missing default constructor of type", e);
        } catch (final InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            throw new ConversionException("Cannot construct type", e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct type", e);
        } catch (final IOException e) {
            throw new StreamException("Cannot externalize " + type.getClass(), e);
        }
    }

    private Object readResolve() {
        serializationMembers = new SerializationMembers();
        return this;
    }
}
