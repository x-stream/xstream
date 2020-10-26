/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2013, 2014, 2015, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 25. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a dynamic proxy to XML, storing the implemented interfaces and handler.
 *
 * @author Joe Walnes
 */
public class DynamicProxyConverter implements Converter {

    private final ClassLoaderReference classLoaderReference;
    private final Mapper mapper;

    /**
     * @deprecated As of 1.4.5 use {@link #DynamicProxyConverter(Mapper, ClassLoaderReference)}
     */
    @Deprecated
    public DynamicProxyConverter(final Mapper mapper) {
        this(mapper, DynamicProxyConverter.class.getClassLoader());
    }

    /**
     * Construct a DynamicProxyConverter.
     *
     * @param mapper the Mapper chain
     * @param classLoaderReference the reference to the {@link ClassLoader} of the XStream instance
     * @since 1.4.5
     */
    public DynamicProxyConverter(final Mapper mapper, final ClassLoaderReference classLoaderReference) {
        this.classLoaderReference = classLoaderReference;
        this.mapper = mapper;
    }

    /**
     * @deprecated As of 1.4.5 use {@link #DynamicProxyConverter(Mapper, ClassLoaderReference)}
     */
    @Deprecated
    public DynamicProxyConverter(final Mapper mapper, final ClassLoader classLoader) {
        this(mapper, new ClassLoaderReference(classLoader));
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && (type.equals(DynamicProxyMapper.DynamicProxy.class) || Proxy.isProxyClass(type));
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(source);
        addInterfacesToXml(source, writer);
        writer.startNode("handler");
        final String attributeName = mapper.aliasForSystemAttribute("class");
        if (attributeName != null) {
            writer.addAttribute(attributeName, mapper.serializedClass(invocationHandler.getClass()));
        }
        context.convertAnother(invocationHandler);
        writer.endNode();
    }

    private void addInterfacesToXml(final Object source, final HierarchicalStreamWriter writer) {
        final Class<?>[] interfaces = source.getClass().getInterfaces();
        for (final Class<?> currentInterface : interfaces) {
            writer.startNode("interface");
            writer.setValue(mapper.serializedClass(currentInterface));
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final List<Class<?>> interfaces = new ArrayList<>();
        InvocationHandler handler = null;
        Class<?> handlerType = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            final String elementName = reader.getNodeName();
            if (elementName.equals("interface")) {
                interfaces.add(mapper.realClass(reader.getValue()));
            } else if (elementName.equals("handler")) {
                final String attributeName = mapper.aliasForSystemAttribute("class");
                if (attributeName != null) {
                    handlerType = mapper.realClass(reader.getAttribute(attributeName));
                    break;
                }
            }
            reader.moveUp();
        }
        if (handlerType == null) {
            throw new ConversionException("No InvocationHandler specified for dynamic proxy");
        }
        final Class<?>[] interfacesAsArray = new Class[interfaces.size()];
        interfaces.toArray(interfacesAsArray);
        Object proxy = null;
        if (Reflections.HANDLER != null) { // we will not be able to resolve references to the proxy
            proxy = Proxy.newProxyInstance(classLoaderReference.getReference(), interfacesAsArray, Reflections.DUMMY);
        }
        handler = (InvocationHandler)context.convertAnother(proxy, handlerType);
        reader.moveUp();
        if (Reflections.HANDLER != null) {
            Fields.write(Reflections.HANDLER, proxy, handler);
        } else {
            proxy = Proxy.newProxyInstance(classLoaderReference.getReference(), interfacesAsArray, handler);
        }
        return proxy;
    }

    private static class Reflections {

        private static final Field HANDLER = Fields.locate(Proxy.class, InvocationHandler.class, false);
        private static final InvocationHandler DUMMY = new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return null;
            }
        };
    }
}
