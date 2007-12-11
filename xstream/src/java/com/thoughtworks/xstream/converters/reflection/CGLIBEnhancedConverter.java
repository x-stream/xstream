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
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.Mapper;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Converts a proxy created by the CGLIB {@link Enhancer}. Such a proxy is recreated while deserializing the proxy. The
 * converter does only work, if<br>
 * <ul>
 * <li>the DefaultNamingPolicy is used for the proxy's name</li>
 * <li>only one CAllback is registered</li>
 * <li>a possible super class has at least a protected default constructor</li>
 * </ul>
 * Note, that the this converter relies on the CGLIBMapper.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CGLIBEnhancedConverter extends SerializableConverter {

    // An alternative implementation is possible by using Enhancer.setCallbackType and
    // Enhancer.createClass().
    // In this case the converter must be derived from the AbstractReflectionConverter,
    // the proxy info must be written/read in a separate structure first, then the
    // Enhancer must create the type and at last the functionality of the ReflectionConverter
    // must be used to create the instance. But let's see user feedback first.
    // No support for multiple callbacks though ...

    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
    private static String CALLBACK_MARKER = "CGLIB$CALLBACK_";
    private transient Map fieldCache;

    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider));
        this.fieldCache = new HashMap();
    }

    public boolean canConvert(Class type) {
        return (Enhancer.isEnhanced(type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0)
                || type == CGLIBMapper.Marker.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Class type = source.getClass();
        boolean hasFactory = Factory.class.isAssignableFrom(type);
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, "type", type);
        context.convertAnother(type.getSuperclass());
        writer.endNode();
        writer.startNode("interfaces");
        Class[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == Factory.class) {
                continue;
            }
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedClass(interfaces[i].getClass()), interfaces[i].getClass());
            context.convertAnother(interfaces[i]);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("hasFactory");
        writer.setValue(String.valueOf(hasFactory && type.getSuperclass() != Object.class));
        writer.endNode();
        Callback[] callbacks = hasFactory ? ((Factory)source).getCallbacks() : getCallbacks(source);
        if (callbacks.length > 1) {
            throw new ConversionException("Cannot handle CGLIB enhanced proxies with multiple callbacks");
        }
        boolean isInterceptor = MethodInterceptor.class.isAssignableFrom(callbacks[0].getClass());

        ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedClass(callbacks[0].getClass()), callbacks[0].getClass());
        context.convertAnother(callbacks[0]);
        writer.endNode();
        try {
            final Field field = type.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, "serialVersionUID", String.class);
            writer.setValue(String.valueOf(serialVersionUID));
            writer.endNode();
        } catch (NoSuchFieldException e) {
            // OK, ignore
        } catch (IllegalAccessException e) {
            // OK, ignore
        }
        if (isInterceptor && type.getSuperclass() != Object.class) {
            writer.startNode("instance");
            super.doMarshalConditionally(source, writer, context);
            writer.endNode();
        }
    }

    private Callback[] getCallbacks(Object source) {
        Class type = source.getClass();
        List fields = (List)fieldCache.get(type.getName());
        if (fields == null) {
            fields = new ArrayList();
            fieldCache.put(type.getName(), fields);
            for (int i = 0; true; ++i) {
                try {
                    Field field = type.getDeclaredField(CALLBACK_MARKER + i);
                    field.setAccessible(true);
                    fields.add(field);
                } catch (NoSuchFieldException e) {
                    break;
                }
            }
        }
        List list = new ArrayList();
        for (int i = 0; i < fields.size(); ++i) {
            try {
                Field field = (Field)fields.get(i);
                list.add(field.get(source));
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Access to "
                        + type.getName()
                        + "."
                        + CALLBACK_MARKER
                        + i
                        + " not allowed");
            }
        }
        return (Callback[])list.toArray(new Callback[list.size()]);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final Enhancer enhancer = new Enhancer();
        reader.moveDown();
        enhancer.setSuperclass((Class)context.convertAnother(null, Class.class));
        reader.moveUp();
        reader.moveDown();
        List interfaces = new ArrayList();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            interfaces.add(context.convertAnother(null, mapper.realClass(reader.getNodeName())));
            reader.moveUp();
        }
        enhancer.setInterfaces((Class[])interfaces.toArray(new Class[interfaces.size()]));
        reader.moveUp();
        reader.moveDown();
        enhancer.setUseFactory(Boolean.getBoolean(reader.getValue()));
        reader.moveUp();
        reader.moveDown();
        enhancer.setCallback((Callback)context.convertAnother(null, mapper.realClass(reader.getNodeName())));
        reader.moveUp();
        Object result = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("serialVersionUID")) {
                enhancer.setSerialVersionUID(Long.valueOf(reader.getValue()));
            } else if (reader.getNodeName().equals("instance")) {
                result = enhancer.create();
                super.doUnmarshalConditionally(result, reader, context);
            }
            reader.moveUp();
        }
        return serializationMethodInvoker.callReadResolve(result == null ? enhancer.create() : result);
    }

    protected List hierarchyFor(Class type) {
        List typeHierarchy = super.hierarchyFor(type);
        // drop the CGLIB proxy
        typeHierarchy.remove(typeHierarchy.size()-1);
        return typeHierarchy;
    }

    private Object readResolve() {
        fieldCache = new HashMap();
        return this;
    }

    private static class CGLIBFilteringReflectionProvider extends ReflectionProviderWrapper {

        public CGLIBFilteringReflectionProvider(final ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        public void visitSerializableFields(final Object object, final Visitor visitor) {
            wrapped.visitSerializableFields(object, new Visitor() {
                public void visit(String name, Class type, Class definedIn, Object value) {
                    if (!name.startsWith("CGLIB$")) {
                        visitor.visit(name, type, definedIn, value);
                    }
                }
            });
        }
    }
}
