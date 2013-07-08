/*
 * Copyright (C) 2008, 2009, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.cache.products;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.core.util.TypedNull;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LocalConversionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.model.Five;
import com.thoughtworks.xstream.tools.benchmark.model.One;
import com.thoughtworks.xstream.tools.benchmark.model.SerializableFive;
import com.thoughtworks.xstream.tools.benchmark.model.SerializableOne;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Uses XStream with the XPP driver.
 * 
 * @author J&ouml;rg Schaible
 */
public abstract class XStreamCache implements Product {

    private final XStream xstream;

    public XStreamCache() {
        ClassLoaderReference classLoaderReference = new ClassLoaderReference(
            new CompositeClassLoader());
        DefaultConverterLookup converterLookup = new DefaultConverterLookup();
        xstream = new XStream(
            JVM.newReflectionProvider(), new XppDriver(), classLoaderReference, buildMapper(
                getMappers(), classLoaderReference, converterLookup), converterLookup, converterLookup);
        xstream.alias("one", One.class);
        xstream.alias("five", Five.class);
        xstream.alias("ser-one", SerializableOne.class);
        xstream.alias("ser-five", SerializableFive.class);
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    protected List getMappers() {
        List mappers = new ArrayList();
        mappers.add(DefaultMapper.class);
        if (JVM.loadClassForName("net.sf.cglib.proxy.Enhancer") != null) {
            mappers.add(JVM.loadClassForName("com.thoughtworks.xstream.mapper.CGLIBMapper"));
        }
        mappers.add(DynamicProxyMapper.class);
        mappers.add(ClassAliasingMapper.class);
        mappers.add(FieldAliasingMapper.class);
        mappers.add(AttributeAliasingMapper.class);
        mappers.add(ImplicitCollectionMapper.class);
        mappers.add(OuterClassMapper.class);
        mappers.add(ArrayMapper.class);
        mappers.add(LocalConversionMapper.class);
        mappers.add(DefaultImplementationsMapper.class);
        if (JVM.is15()) {
            mappers.add(JVM.loadClassForName("com.thoughtworks.xstream.mapper.EnumMapper"));
        } else {
            mappers.add(AttributeMapper.class);
        }
        mappers.add(ImmutableTypesMapper.class);
        if (JVM.is15()) {
            mappers.add(JVM.loadClassForName("com.thoughtworks.xstream.mapper.AnnotationMapper"));
        }
        return mappers;
    }

    private Mapper buildMapper(List mappers, ClassLoaderReference classLoaderReference,
        ConverterLookup converterLookup) {
        final Object[] arguments = new Object[]{
            new TypedNull(Mapper.class), converterLookup, classLoaderReference,
            JVM.newReflectionProvider()};
        for (final Iterator iter = mappers.iterator(); iter.hasNext();) {
            final Class mapperType = (Class)iter.next();
            try {
                arguments[0] = DependencyInjectionFactory.newInstance(mapperType, arguments);
            } catch (Exception e) {
                throw new InitializationException("Could not instantiate mapper : "
                    + mapperType.getName(), e);
            }
        }
        return (Mapper)arguments[0];
    }
}
