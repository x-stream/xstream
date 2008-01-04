/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.cache.products;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.benchmark.cache.model.Five;
import com.thoughtworks.xstream.benchmark.cache.model.One;
import com.thoughtworks.xstream.benchmark.cache.model.SerializableFive;
import com.thoughtworks.xstream.benchmark.cache.model.SerializableOne;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.DefaultConverterLookup;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.AttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LocalConversionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.OuterClassMapper;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;


/**
 * Uses XStream with the XPP driver.
 * 
 * @author J&ouml;rg Schaible
 */
public abstract class XStreamCache implements Product {

    private final XStream xstream;

    public XStreamCache() {
        JVM jvm = new JVM();
        ClassLoaderReference classLoaderReference = new ClassLoaderReference(
            new CompositeClassLoader());
        DefaultConverterLookup converterLookup = new DefaultConverterLookup();
        xstream = new XStream(
            jvm.bestReflectionProvider(), new XppDriver(), classLoaderReference, buildMapper(
                jvm, classLoaderReference, converterLookup), converterLookup);
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

    protected abstract Mapper createCachingMapper(Mapper mapper);

    private Mapper buildMapper(JVM jvm, ClassLoader classLoader, ConverterLookup converterLookup) {
        Mapper mapper = new DefaultMapper(classLoader);
        mapper = new ClassAliasingMapper(mapper);
        mapper = new FieldAliasingMapper(mapper);
        mapper = new AttributeAliasingMapper(mapper);
        mapper = new AttributeMapper(mapper, converterLookup);
        mapper = new ImplicitCollectionMapper(mapper);
        if (jvm.loadClass("net.sf.cglib.proxy.Enhancer") != null) {
            mapper = buildMapperDynamically(
                classLoader, "com.thoughtworks.xstream.mapper.CGLIBMapper",
                new Class[]{Mapper.class}, new Object[]{mapper});
        }
        mapper = new DynamicProxyMapper(mapper);
        if (JVM.is15()) {
            mapper = new EnumMapper(mapper);
        }
        mapper = new OuterClassMapper(mapper);
        mapper = new ArrayMapper(mapper);
        mapper = new LocalConversionMapper(mapper);
        mapper = new DefaultImplementationsMapper(mapper);
        mapper = new ImmutableTypesMapper(mapper);
        if (JVM.is15()) {
            mapper = buildMapperDynamically(
                classLoader, "com.thoughtworks.xstream.mapper.AnnotationMapper", new Class[]{
                    Mapper.class, ConverterRegistry.class, ClassLoader.class,
                    ReflectionProvider.class, JVM.class}, new Object[]{
                    mapper, converterLookup, classLoader, jvm.bestReflectionProvider(), jvm});
        }
        mapper = createCachingMapper(mapper);
        return mapper;
    }

    private Mapper buildMapperDynamically(ClassLoader classLoader, String className,
        Class[] constructorParamTypes, Object[] constructorParamValues) {
        try {
            Class type = Class.forName(className, false, classLoader);
            Constructor constructor = type.getConstructor(constructorParamTypes);
            return (Mapper)constructor.newInstance(constructorParamValues);
        } catch (Exception e) {
            throw new InitializationException("Could not instantiate mapper : " + className, e);
        }
    }

}
