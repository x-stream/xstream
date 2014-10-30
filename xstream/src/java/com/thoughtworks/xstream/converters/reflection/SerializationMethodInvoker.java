/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.FastField;


/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class SerializationMethodInvoker implements Caching {

    private static final Method NO_METHOD = new Object() {
        @SuppressWarnings("unused")
        private void noMethod() {
        }
    }.getClass().getDeclaredMethods()[0];
    private static final FastField[] OBJECT_TYPE_FIELDS = {
        new FastField(Object.class, "readResolve"), new FastField(Object.class, "writeReplace"),
        new FastField(Object.class, "readObject"), new FastField(Object.class, "writeObject")};
    private final ConcurrentMap<FastField, Method> declaredCache = new ConcurrentHashMap<FastField, Method>(
            new HashMap<FastField, Method>());
    private final ConcurrentMap<FastField, Method> resRepCache = new ConcurrentHashMap<FastField, Method>(
            new HashMap<FastField, Method>());
    {
        for (final FastField element : OBJECT_TYPE_FIELDS) {
            declaredCache.put(element, NO_METHOD);
        }
        for (final FastField element : Arrays.copyOf(OBJECT_TYPE_FIELDS, 2)) {
            resRepCache.put(element, NO_METHOD);
        }
    }

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     */
    public Object callReadResolve(final Object result) {
        if (result == null) {
            return null;
        } else {
            final Class<? extends Object> resultType = result.getClass();
            final Method readResolveMethod = getRRMethod(resultType, "readResolve");
            if (readResolveMethod != null) {
                try {
                    return readResolveMethod.invoke(result);
                } catch (final IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call " + resultType.getName() + ".readResolve()", e);
                } catch (final InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call " + resultType.getName() + ".readResolve()", e
                        .getTargetException());
                }
            } else {
                return result;
            }
        }
    }

    public Object callWriteReplace(final Object object) {
        if (object == null) {
            return null;
        } else {
            final Class<? extends Object> objectType = object.getClass();
            final Method writeReplaceMethod = getRRMethod(objectType, "writeReplace");
            if (writeReplaceMethod != null) {
                try {
                    return writeReplaceMethod.invoke(object);
                } catch (final IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call " + objectType.getName() + ".writeReplace()", e);
                } catch (final InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call " + objectType.getName() + ".writeReplace()", e
                        .getTargetException());
                }
            } else {
                return object;
            }
        }
    }

    public boolean supportsReadObject(final Class<?> type, final boolean includeBaseClasses) {
        return getMethod(type, "readObject", includeBaseClasses, ObjectInputStream.class) != null;
    }

    public void callReadObject(final Class<?> type, final Object object, final ObjectInputStream stream) {
        try {
            final Method readObjectMethod = getMethod(type, "readObject", false, ObjectInputStream.class);
            readObjectMethod.invoke(object, stream);
        } catch (final IllegalAccessException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        } catch (final InvocationTargetException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e
                .getTargetException());
        }
    }

    public boolean supportsWriteObject(final Class<?> type, final boolean includeBaseClasses) {
        return getMethod(type, "writeObject", includeBaseClasses, ObjectOutputStream.class) != null;
    }

    public void callWriteObject(final Class<?> type, final Object instance, final ObjectOutputStream stream) {
        try {
            final Method readObjectMethod = getMethod(type, "writeObject", false, ObjectOutputStream.class);
            readObjectMethod.invoke(instance, stream);
        } catch (final IllegalAccessException e) {
            throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()", e);
        } catch (final InvocationTargetException e) {
            throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()", e
                .getTargetException());
        }
    }

    private Method getMethod(final Class<?> type, final String name, final boolean includeBaseclasses,
            final Class<?>... parameterTypes) {
        final Method method = getMethod(type, name, parameterTypes);
        return method == NO_METHOD || !includeBaseclasses && !method.getDeclaringClass().equals(type) ? null : method;
    }

    private Method getMethod(final Class<?> type, final String name, final Class<?>... parameterTypes) {
        if (type == null) {
            return null;
        }
        final FastField method = new FastField(type, name);
        Method result = declaredCache.get(method);

        if (result == null) {
            try {
                result = type.getDeclaredMethod(name, parameterTypes);
                if (!result.isAccessible()) {
                    result.setAccessible(true);
                }
            } catch (final NoSuchMethodException e) {
                result = getMethod(type.getSuperclass(), name, parameterTypes);
            }
            declaredCache.put(method, result);
        }
        return result;
    }

    private Method getRRMethod(final Class<?> type, final String name) {
        final FastField method = new FastField(type, name);
        Method result = resRepCache.get(method);
        if (result == null) {
            result = getMethod(type, name, true);
            if (result != null && result.getDeclaringClass() != type) {
                if ((result.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0) {
                    if ((result.getModifiers() & Modifier.PRIVATE) > 0
                            || type.getPackage() != result.getDeclaringClass().getPackage()) {
                        result = NO_METHOD;
                    }
                }
            } else if (result == null) {
                result = NO_METHOD;
            }
            resRepCache.putIfAbsent(method, result);
        }
        return result == NO_METHOD ? null : result;
    }

    @Override
    public void flushCache() {
        declaredCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
        resRepCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
    }
}
