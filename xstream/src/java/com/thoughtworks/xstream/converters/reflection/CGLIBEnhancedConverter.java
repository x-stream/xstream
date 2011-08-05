/*
 * Copyright (C) 2006, 2007, 2008, 2010, 2011 XStream Committers.
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
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.Mapper;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Converts a proxy created by the CGLIB {@link Enhancer}. Such a proxy is recreated while
 * deserializing the proxy. The converter does only work, if<br>
 * <ul>
 * <li>the DefaultNamingPolicy is used for the proxy's name</li>
 * <li>the proxy uses a factory or only one Callback is registered</li>
 * <li>a possible super class has at least a protected default constructor</li>
 * </ul>
 * Note, that the this converter relies on the CGLIBMapper.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CGLIBEnhancedConverter extends SerializableConverter {
    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
    private static String CALLBACK_MARKER = "CGLIB$CALLBACK_";
    private transient Map fieldCache;

    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoader classLoader) {
        super(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider), classLoader);
        this.fieldCache = new HashMap();
    }

    /**
     * @deprecated As of 1.4 use {@link #CGLIBEnhancedConverter(Mapper, ReflectionProvider, ClassLoader)}
     */
    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider), null);
    }

    public boolean canConvert(Class type) {
        return (Enhancer.isEnhanced(type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0)
            || type == CGLIBMapper.Marker.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
        Class type = source.getClass();
        boolean hasFactory = Factory.class.isAssignableFrom(type);
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, "type", type);
        context.convertAnother(type.getSuperclass());
        writer.endNode();
        writer.startNode("interfaces");
        Class[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++ ) {
            if (interfaces[i] == Factory.class) {
                continue;
            }
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper
                .serializedClass(interfaces[i].getClass()), interfaces[i].getClass());
            context.convertAnother(interfaces[i]);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("hasFactory");
        writer.setValue(String.valueOf(hasFactory));
        writer.endNode();
        Map callbackIndexMap = null;
        Callback[] callbacks = hasFactory
            ? ((Factory)source).getCallbacks()
            : getCallbacks(source);
        if (callbacks.length > 1) {
            if (hasFactory) {
                callbackIndexMap = createCallbackIndexMap((Factory)source);
            } else {
                ConversionException exception = new ConversionException(
                    "Cannot handle CGLIB enhanced proxies without factory that have multiple callbacks");
                exception.add("proxy superclass", type.getSuperclass().getName());
                exception.add("number of callbacks", String.valueOf(callbacks.length));
                throw exception;
            }
            writer.startNode("callbacks");
            writer.startNode("mapping");
            context.convertAnother(callbackIndexMap);
            writer.endNode();
        }
        boolean hasInterceptor = false;
        for (int i = 0; i < callbacks.length; i++ ) {
            final Callback callback = callbacks[i];
            if (callback == null) {
                String name = mapper.serializedClass(null);
                writer.startNode(name);
                writer.endNode();
            } else {
                hasInterceptor = hasInterceptor
                    || MethodInterceptor.class.isAssignableFrom(callback.getClass());
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper
                    .serializedClass(callback.getClass()), callback.getClass());
                context.convertAnother(callback);
                writer.endNode();
            }
        }
        if (callbacks.length > 1) {
            writer.endNode();
        }
        try {
            final Field field = type.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            ExtendedHierarchicalStreamWriterHelper.startNode(
                writer, "serialVersionUID", String.class);
            writer.setValue(String.valueOf(serialVersionUID));
            writer.endNode();
        } catch (NoSuchFieldException e) {
            // OK, ignore
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Access to serialVersionUID of "
                + type.getName()
                + " not allowed");
        }
        if (hasInterceptor) {
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
                Object callback = field.get(source);
                list.add(callback);
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

    private Map createCallbackIndexMap(Factory source) {
        Callback[] originalCallbacks = source.getCallbacks();
        Callback[] reverseEngineeringCallbacks = new Callback[originalCallbacks.length];
        Map callbackIndexMap = new HashMap();
        int idxNoOp = -1;
        for (int i = 0; i < originalCallbacks.length; i++ ) {
            Callback callback = originalCallbacks[i];
            if (callback == null) {
                reverseEngineeringCallbacks[i] = null;
            } else if (NoOp.class.isAssignableFrom(callback.getClass())) {
                reverseEngineeringCallbacks[i] = NoOp.INSTANCE;
                idxNoOp = i;
            } else {
                reverseEngineeringCallbacks[i] = createReverseEngineeredCallbackOfProperType(
                    callback, i, callbackIndexMap);
            }
        }

        try {
            source.setCallbacks(reverseEngineeringCallbacks);
            final Set interfaces = new HashSet();
            final Set methods = new HashSet();
            Class type = source.getClass();
            do {
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
                methods.addAll(Arrays.asList(type.getMethods()));
                Class[] implementedInterfaces = type.getInterfaces();
                interfaces.addAll(Arrays.asList(implementedInterfaces));
                type = type.getSuperclass();
            } while (type != null);
            for (final Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
                type = (Class)iterator.next();
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
            }
            for (final Iterator iter = methods.iterator(); iter.hasNext();) {
                final Method method = (Method)iter.next();
                method.setAccessible(true);
                if (Factory.class.isAssignableFrom(method.getDeclaringClass())
                    || (method.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) > 0) {
                    iter.remove();
                    continue;
                }
                Class[] parameterTypes = method.getParameterTypes();
                Method calledMethod = method;
                try {
                    if ((method.getModifiers() & Modifier.ABSTRACT) > 0) {
                        calledMethod = source.getClass().getMethod(
                            method.getName(), method.getParameterTypes());
                    }
                    callbackIndexMap.put(null, method);
                    calledMethod.invoke(source, parameterTypes == null
                        ? (Object[])null
                        : createNullArguments(parameterTypes));
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Access to "
                        + calledMethod
                        + " not allowed");
                } catch (InvocationTargetException e) {
                    // OK, ignore
                } catch (NoSuchMethodException e) {
                    ConversionException exception = new ConversionException(
                        "CGLIB enhanced proxies wit abstract nethod that has not been implemented");
                    exception.add("proxy superclass", type.getSuperclass().getName());
                    exception.add("method", method.toString());
                    throw exception;
                }
                if (callbackIndexMap.containsKey(method)) {
                    iter.remove();
                }
            }
            if (idxNoOp >= 0) {
                Integer idx = new Integer(idxNoOp);
                for (final Iterator iter = methods.iterator(); iter.hasNext();) {
                    callbackIndexMap.put(iter.next(), idx);
                }
            }
        } finally {
            source.setCallbacks(originalCallbacks);
        }

        callbackIndexMap.remove(null);
        return callbackIndexMap;
    }

    private Object[] createNullArguments(Class[] parameterTypes) {
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; i++ ) {
            Class type = parameterTypes[i];
            if (type.isPrimitive()) {
                if (type == byte.class) {
                    arguments[i] = new Byte((byte)0);
                } else if (type == short.class) {
                    arguments[i] = new Short((short)0);
                } else if (type == int.class) {
                    arguments[i] = new Integer(0);
                } else if (type == long.class) {
                    arguments[i] = new Long(0);
                } else if (type == float.class) {
                    arguments[i] = new Float(0);
                } else if (type == double.class) {
                    arguments[i] = new Double(0);
                } else if (type == char.class) {
                    arguments[i] = new Character('\0');
                } else {
                    arguments[i] = Boolean.FALSE;
                }
            }
        }
        return arguments;
    }

    private Callback createReverseEngineeredCallbackOfProperType(Callback callback, int index,
        Map callbackIndexMap) {
        Class iface = null;
        Class[] interfaces = callback.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++ ) {
            if (Callback.class.isAssignableFrom(interfaces[i])) {
                iface = interfaces[i];
                if (iface == Callback.class) {
                    ConversionException exception = new ConversionException(
                        "Cannot handle CGLIB callback");
                    exception.add("CGLIB callback type", callback.getClass().getName());
                    throw exception;
                }
                interfaces = iface.getInterfaces();
                if (Arrays.asList(interfaces).contains(Callback.class)) {
                    break;
                }
                i = -1;
            }
        }
        return (Callback)Proxy.newProxyInstance(
            iface.getClassLoader(), new Class[]{iface},
            new ReverseEngineeringInvocationHandler(index, callbackIndexMap));
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
            interfaces
                .add(context.convertAnother(null, mapper.realClass(reader.getNodeName())));
            reader.moveUp();
        }
        enhancer.setInterfaces((Class[])interfaces.toArray(new Class[interfaces.size()]));
        reader.moveUp();
        reader.moveDown();
        boolean useFactory = Boolean.valueOf(reader.getValue()).booleanValue();
        enhancer.setUseFactory(useFactory);
        reader.moveUp();

        List callbacksToEnhance = new ArrayList();
        List callbacks = new ArrayList();
        Map callbackIndexMap = null;
        reader.moveDown();
        if ("callbacks".equals(reader.getNodeName())) {
            reader.moveDown();
            callbackIndexMap = (Map)context.convertAnother(null, HashMap.class);
            reader.moveUp();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                readCallback(reader, context, callbacksToEnhance, callbacks);
                reader.moveUp();
            }
        } else {
            readCallback(reader, context, callbacksToEnhance, callbacks);
        }
        enhancer.setCallbacks((Callback[])callbacksToEnhance
            .toArray(new Callback[callbacksToEnhance.size()]));
        if (callbackIndexMap != null) {
            enhancer.setCallbackFilter(new ReverseEngineeredCallbackFilter(callbackIndexMap));
        }
        reader.moveUp();
        Object result = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("serialVersionUID")) {
                enhancer.setSerialVersionUID(Long.valueOf(reader.getValue()));
            } else if (reader.getNodeName().equals("instance")) {
                result = create(enhancer, callbacks, useFactory);
                super.doUnmarshalConditionally(result, reader, context);
            }
            reader.moveUp();
        }
        if (result == null) {
            result = create(enhancer, callbacks, useFactory);
        }
        return serializationMethodInvoker.callReadResolve(result);
    }

    private void readCallback(HierarchicalStreamReader reader, UnmarshallingContext context,
        List callbacksToEnhance, List callbacks) {
        Callback callback = (Callback)context.convertAnother(null, mapper.realClass(reader
            .getNodeName()));
        callbacks.add(callback);
        if (callback == null) {
            callbacksToEnhance.add(NoOp.INSTANCE);
        } else {
            callbacksToEnhance.add(callback);
        }
    }

    private Object create(final Enhancer enhancer, List callbacks, boolean useFactory) {
        Object result = enhancer.create();
        if (useFactory) {
            ((Factory)result).setCallbacks((Callback[])callbacks.toArray(new Callback[callbacks
                .size()]));
        }
        return result;
    }

    protected List hierarchyFor(Class type) {
        List typeHierarchy = super.hierarchyFor(type);
        // drop the CGLIB proxy
        typeHierarchy.remove(typeHierarchy.size() - 1);
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

    private static final class ReverseEngineeringInvocationHandler implements InvocationHandler {
        private final Integer index;
        private final Map indexMap;

        public ReverseEngineeringInvocationHandler(int index, Map indexMap) {
            this.indexMap = indexMap;
            this.index = new Integer(index);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            indexMap.put(indexMap.get(null), index);
            return null;
        }
    }

    private static class ReverseEngineeredCallbackFilter implements CallbackFilter {

        private final Map callbackIndexMap;

        public ReverseEngineeredCallbackFilter(Map callbackIndexMap) {
            this.callbackIndexMap = callbackIndexMap;
        }

        public int accept(Method method) {
            if (!callbackIndexMap.containsKey(method)) {
                ConversionException exception = new ConversionException(
                    "CGLIB callback not detected in reverse engineering");
                exception.add("CGLIB callback", method.toString());
                throw exception;
            }
            return ((Integer)callbackIndexMap.get(method)).intValue();
        }

    }
}
