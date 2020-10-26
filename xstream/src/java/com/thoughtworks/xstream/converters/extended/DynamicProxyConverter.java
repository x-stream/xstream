/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
