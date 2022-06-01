/*
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2013, 2014, 2015, 2016, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
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


/**
 * Converts a proxy created by the CGLIB {@link Enhancer}. Such a proxy is recreated while deserializing the proxy. The
 * converter does only work, if<br>
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
    private transient Map<String, List<Field>> fieldCache;

    /**
     * Construct a CGLIBEnhancedConverter.
     *
     * @param mapper the mapper chain instance
     * @param reflectionProvider the reflection provider
     * @param classLoaderReference the reference to the {@link ClassLoader} of the XStream instance
     * @since 1.4.5
     */
    public CGLIBEnhancedConverter(
            final Mapper mapper, final ReflectionProvider reflectionProvider,
            final ClassLoaderReference classLoaderReference) {
        super(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider), classLoaderReference);
        fieldCache = new HashMap<>();
    }

    /**
     * @deprecated As of 1.4.5 use {@link #CGLIBEnhancedConverter(Mapper, ReflectionProvider, ClassLoaderReference)}
     */
    @Deprecated
    public CGLIBEnhancedConverter(
            final Mapper mapper, final ReflectionProvider reflectionProvider, final ClassLoader classLoader) {
        super(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider), classLoader);
        fieldCache = new HashMap<>();
    }

    /**
     * @deprecated As of 1.4 use {@link #CGLIBEnhancedConverter(Mapper, ReflectionProvider, ClassLoaderReference)}
     */
    @Deprecated
    public CGLIBEnhancedConverter(final Mapper mapper, final ReflectionProvider reflectionProvider) {
        this(mapper, new CGLIBFilteringReflectionProvider(reflectionProvider), CGLIBEnhancedConverter.class
            .getClassLoader());
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && Enhancer.isEnhanced(type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0
            || type == CGLIBMapper.Marker.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Class<?> type = source.getClass();
        final boolean hasFactory = Factory.class.isAssignableFrom(type);
        writer.startNode("type", type);
        context.convertAnother(type.getSuperclass());
        writer.endNode();
        writer.startNode("interfaces");
        final Class<?>[] interfaces = type.getInterfaces();
        for (final Class<?> interface1 : interfaces) {
            if (interface1 == Factory.class) {
                continue;
            }
            writer.startNode(mapper.serializedClass(interface1.getClass()), interface1.getClass());
            context.convertAnother(interface1);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("hasFactory");
        writer.setValue(String.valueOf(hasFactory));
        writer.endNode();
        Map<?, ?> callbackIndexMap = null;
        final Callback[] callbacks = hasFactory ? ((Factory)source).getCallbacks() : getCallbacks(source);
        if (callbacks.length > 1) {
            if (hasFactory) {
                callbackIndexMap = createCallbackIndexMap((Factory)source);
            } else {
                final ConversionException exception = new ConversionException(
                    "Cannot handle CGLIB enhanced proxies without factory that have multiple callbacks");
                exception.add("proxy-superclass", type.getSuperclass().getName());
                exception.add("number-of-callbacks", String.valueOf(callbacks.length));
                throw exception;
            }
            writer.startNode("callbacks");
            writer.startNode("mapping");
            context.convertAnother(callbackIndexMap);
            writer.endNode();
        }
        boolean hasInterceptor = false;
        for (final Callback callback : callbacks) {
            if (callback == null) {
                final String name = mapper.serializedClass(null);
                writer.startNode(name);
                writer.endNode();
            } else {
                hasInterceptor = hasInterceptor || MethodInterceptor.class.isAssignableFrom(callback.getClass());
                writer.startNode(mapper.serializedClass(callback.getClass()), callback.getClass());
                context.convertAnother(callback);
                writer.endNode();
            }
        }
        if (callbacks.length > 1) {
            writer.endNode();
        }
        try {
            final Field field = type.getDeclaredField("serialVersionUID");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            final long serialVersionUID = field.getLong(null);
            writer.startNode("serialVersionUID", String.class);
            writer.setValue(String.valueOf(serialVersionUID));
            writer.endNode();
        } catch (final NoSuchFieldException e) {
            // OK, ignore
        } catch (final IllegalAccessException e) {
            final ObjectAccessException exception = new ObjectAccessException("Cannot access field", e);
            exception.add("field", type.getName() + ".serialVersionUID");
            throw exception;
        }
        if (hasInterceptor) {
            writer.startNode("instance");
            super.doMarshalConditionally(source, writer, context);
            writer.endNode();
        }
    }

    private Callback[] getCallbacks(final Object source) {
        final Class<?> type = source.getClass();
        List<Field> fields = fieldCache.get(type.getName());
        if (fields == null) {
            fields = new ArrayList<>();
            fieldCache.put(type.getName(), fields);
            for (int i = 0; true; ++i) {
                try {
                    final Field field = type.getDeclaredField(CALLBACK_MARKER + i);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    fields.add(field);
                } catch (final NoSuchFieldException e) {
                    break;
                }
            }
        }
        final List<Callback> list = new ArrayList<>();
        for (int i = 0; i < fields.size(); ++i) {
            try {
                final Field field = fields.get(i);
                final Callback callback = (Callback)field.get(source);
                list.add(callback);
            } catch (final IllegalAccessException e) {
                final ObjectAccessException exception = new ObjectAccessException("Cannot access field", e);
                exception.add("field", type.getName() + "." + CALLBACK_MARKER + i);
                throw exception;
            }
        }
        return list.toArray(new Callback[list.size()]);
    }

    private Map<? super Object, ? super Object> createCallbackIndexMap(final Factory source) {
        final Callback[] originalCallbacks = source.getCallbacks();
        final Callback[] reverseEngineeringCallbacks = new Callback[originalCallbacks.length];
        final Map<? super Object, ? super Object> callbackIndexMap = new HashMap<>();
        int idxNoOp = -1;
        for (int i = 0; i < originalCallbacks.length; i++) {
            final Callback callback = originalCallbacks[i];
            if (callback == null) {
                reverseEngineeringCallbacks[i] = null;
            } else if (NoOp.class.isAssignableFrom(callback.getClass())) {
                reverseEngineeringCallbacks[i] = NoOp.INSTANCE;
                idxNoOp = i;
            } else {
                reverseEngineeringCallbacks[i] = createReverseEngineeredCallbackOfProperType(callback, i,
                    callbackIndexMap);
            }
        }

        try {
            source.setCallbacks(reverseEngineeringCallbacks);
            final Set<Class<?>> interfaces = new HashSet<>();
            final Set<Method> methods = new HashSet<>();
            Class<?> type = source.getClass();
            do {
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
                methods.addAll(Arrays.asList(type.getMethods()));
                final Class<?>[] implementedInterfaces = type.getInterfaces();
                interfaces.addAll(Arrays.asList(implementedInterfaces));
                type = type.getSuperclass();
            } while (type != null);
            for (final Iterator<Class<?>> iterator = interfaces.iterator(); iterator.hasNext();) {
                type = iterator.next();
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
            }
            for (final Iterator<Method> iter = methods.iterator(); iter.hasNext();) {
                final Method method = iter.next();
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (Factory.class.isAssignableFrom(method.getDeclaringClass())
                    || (method.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) > 0) {
                    iter.remove();
                    continue;
                }
                final Class<?>[] parameterTypes = method.getParameterTypes();
                Method calledMethod = method;
                try {
                    if ((method.getModifiers() & Modifier.ABSTRACT) > 0) {
                        calledMethod = source.getClass().getMethod(method.getName(), method.getParameterTypes());
                    }
                    callbackIndexMap.put(null, method);
                    calledMethod.invoke(source, parameterTypes == null
                        ? (Object[])null
                        : createNullArguments(parameterTypes));
                } catch (final IllegalAccessException e) {
                    final ObjectAccessException exception = new ObjectAccessException("Cannot access method", e);
                    exception.add("method", calledMethod.toString());
                    throw exception;
                } catch (final InvocationTargetException e) {
                    // OK, ignore
                } catch (final NoSuchMethodException e) {
                    final ConversionException exception = new ConversionException(
                        "CGLIB enhanced proxies wit abstract nethod that has not been implemented");
                    exception.add("proxy-superclass", type.getSuperclass().getName());
                    exception.add("method", method.toString());
                    throw exception;
                }
                if (callbackIndexMap.containsKey(method)) {
                    iter.remove();
                }
            }
            if (idxNoOp >= 0) {
                final Integer idx = Integer.valueOf(idxNoOp);
                for (final Method method : methods) {
                    callbackIndexMap.put(method, idx);
                }
            }
        } finally {
            source.setCallbacks(originalCallbacks);
        }

        callbackIndexMap.remove(null);
        return callbackIndexMap;
    }

    private Object[] createNullArguments(final Class<?>[] parameterTypes) {
        final Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; i++) {
            final Class<?> type = parameterTypes[i];
            if (type.isPrimitive()) {
                if (type == byte.class) {
                    arguments[i] = Byte.valueOf((byte)0);
                } else if (type == short.class) {
                    arguments[i] = Short.valueOf((short)0);
                } else if (type == int.class) {
                    arguments[i] = Integer.valueOf(0);
                } else if (type == long.class) {
                    arguments[i] = Long.valueOf(0);
                } else if (type == float.class) {
                    arguments[i] = Float.valueOf(0);
                } else if (type == double.class) {
                    arguments[i] = Double.valueOf(0);
                } else if (type == char.class) {
                    arguments[i] = Character.valueOf('\0');
                } else {
                    arguments[i] = Boolean.FALSE;
                }
            }
        }
        return arguments;
    }

    private Callback createReverseEngineeredCallbackOfProperType(final Callback callback, final int index,
            final Map<? super Object, ? super Object> callbackIndexMap) {
        Class<?> iface = null;
        Class<?>[] interfaces = callback.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (Callback.class.isAssignableFrom(interfaces[i])) {
                iface = interfaces[i];
                if (iface == Callback.class) {
                    final ConversionException exception = new ConversionException("Cannot handle CGLIB callback");
                    exception.add("CGLIB-callback-type", callback.getClass().getName());
                    throw exception;
                }
                interfaces = iface.getInterfaces();
                if (Arrays.asList(interfaces).contains(Callback.class)) {
                    break;
                }
                i = -1;
            }
        }
        return (Callback)Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface},
            new ReverseEngineeringInvocationHandler(index, callbackIndexMap));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Enhancer enhancer = new Enhancer();
        reader.moveDown();
        enhancer.setSuperclass((Class<?>)context.convertAnother(null, Class.class));
        reader.moveUp();
        reader.moveDown();
        final List<Class<?>> interfaces = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            interfaces.add((Class<?>)context.convertAnother(null, mapper.realClass(reader.getNodeName())));
            reader.moveUp();
        }
        enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
        reader.moveUp();
        reader.moveDown();
        final boolean useFactory = Boolean.valueOf(reader.getValue()).booleanValue();
        enhancer.setUseFactory(useFactory);
        reader.moveUp();

        final List<Callback> callbacksToEnhance = new ArrayList<>();
        final List<Callback> callbacks = new ArrayList<>();
        Map<Method, Integer> callbackIndexMap = null;
        reader.moveDown();
        if ("callbacks".equals(reader.getNodeName())) {
            reader.moveDown();
            @SuppressWarnings("unchecked")
            final Map<Method, Integer> typedMap = (Map<Method, Integer>)context.convertAnother(null, HashMap.class);
            callbackIndexMap = typedMap;
            reader.moveUp();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                readCallback(reader, context, callbacksToEnhance, callbacks);
                reader.moveUp();
            }
        } else {
            readCallback(reader, context, callbacksToEnhance, callbacks);
        }
        enhancer.setCallbacks(callbacksToEnhance.toArray(new Callback[callbacksToEnhance.size()]));
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
        return serializationMembers.callReadResolve(result);
    }

    private void readCallback(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final List<Callback> callbacksToEnhance, final List<Callback> callbacks) {
        final Callback callback = (Callback)context.convertAnother(null, mapper.realClass(reader.getNodeName()));
        callbacks.add(callback);
        if (callback == null) {
            callbacksToEnhance.add(NoOp.INSTANCE);
        } else {
            callbacksToEnhance.add(callback);
        }
    }

    private Object create(final Enhancer enhancer, final List<Callback> callbacks, final boolean useFactory) {
        final Object result = enhancer.create();
        if (useFactory) {
            ((Factory)result).setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
        }
        return result;
    }

    @Override
    protected List<Class<?>> hierarchyFor(final Class<?> type) {
        final List<Class<?>> typeHierarchy = super.hierarchyFor(type);
        // drop the CGLIB proxy
        typeHierarchy.remove(typeHierarchy.size() - 1);
        return typeHierarchy;
    }

    @Override
    protected Object readResolve() {
        super.readResolve();
        fieldCache = new HashMap<>();
        return this;
    }

    private static class CGLIBFilteringReflectionProvider extends ReflectionProviderWrapper {

        public CGLIBFilteringReflectionProvider(final ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        @Override
        public void visitSerializableFields(final Object object, final Visitor visitor) {
            wrapped.visitSerializableFields(object, new Visitor() {
                @Override
                public void visit(final String name, final Class<?> type, final Class<?> definedIn,
                        final Object value) {
                    if (!name.startsWith("CGLIB$")) {
                        visitor.visit(name, type, definedIn, value);
                    }
                }
            });
        }
    }

    private static final class ReverseEngineeringInvocationHandler implements InvocationHandler {
        private final Integer index;
        private final Map<? super Object, ? super Object> indexMap;

        public ReverseEngineeringInvocationHandler(
                final int index, final Map<? super Object, ? super Object> indexMap) {
            this.indexMap = indexMap;
            this.index = Integer.valueOf(index);
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            indexMap.put(indexMap.get(null), index);
            return null;
        }
    }

    private static class ReverseEngineeredCallbackFilter implements CallbackFilter {

        private final Map<Method, Integer> callbackIndexMap;

        public ReverseEngineeredCallbackFilter(final Map<Method, Integer> callbackIndexMap) {
            this.callbackIndexMap = callbackIndexMap;
        }

        @Override
        public int accept(final Method method) {
            if (!callbackIndexMap.containsKey(method)) {
                final ConversionException exception = new ConversionException(
                    "CGLIB callback not detected in reverse engineering");
                exception.add("CGLIB-callback", method.toString());
                throw exception;
            }
            return callbackIndexMap.get(method).intValue();
        }

    }
}
