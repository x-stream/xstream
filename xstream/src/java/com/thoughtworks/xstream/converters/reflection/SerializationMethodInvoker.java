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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.FastField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform
 * reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class SerializationMethodInvoker implements Caching {

    private static final Method NO_METHOD = (new Object() {
        private void noMethod() {
        }
    }).getClass().getDeclaredMethods()[0];
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class[] EMPTY_CLASSES = new Class[0];
    private static final FastField[] OBJECT_TYPE_FIELDS = {
        new FastField(Object.class, "readResolve"), 
        new FastField(Object.class, "writeReplace"), 
        new FastField(Object.class, "readObject"), 
        new FastField(Object.class, "writeObject")
    };
    private Map declaredCache = Collections.synchronizedMap(new HashMap());
    private Map resRepCache = Collections.synchronizedMap(new HashMap());
    {
        for(int i = 0; i < OBJECT_TYPE_FIELDS.length; ++i) {
            declaredCache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
        for(int i = 0; i < 2; ++i) {
            resRepCache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
    }

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     */
    public Object callReadResolve(Object result) {
        if (result == null) {
            return null;
        } else {
            final Class resultType = result.getClass();
            final Method readResolveMethod = getRRMethod(resultType, "readResolve");
            if (readResolveMethod != null) {
                try {
                    return readResolveMethod.invoke(result, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call "
                        + resultType.getName()
                        + ".readResolve()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call "
                        + resultType.getName()
                        + ".readResolve()", e.getTargetException());
                }
            } else {
                return result;
            }
        }
    }

    public Object callWriteReplace(Object object) {
        if (object == null) {
            return null;
        } else {
            final Class objectType = object.getClass();
            final Method writeReplaceMethod = getRRMethod(objectType, "writeReplace");
            if (writeReplaceMethod != null) {
                try {
                    return writeReplaceMethod.invoke(object, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call "
                        + objectType.getName()
                        + ".writeReplace()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call "
                        + objectType.getName()
                        + ".writeReplace()", e.getTargetException());
                }
            } else {
                return object;
            }
        }
    }

    public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
        return getMethod(
            type, "readObject", new Class[]{ObjectInputStream.class}, includeBaseClasses) != null;
    }

    public void callReadObject(Class type, Object object, ObjectInputStream stream) {
        try {
            Method readObjectMethod = getMethod(
                type, "readObject", new Class[]{ObjectInputStream.class}, false);
            readObjectMethod.invoke(object, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call "
                + object.getClass().getName()
                + ".readObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call "
                + object.getClass().getName()
                + ".readObject()", e.getTargetException());
        }
    }

    public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
        return getMethod(
            type, "writeObject", new Class[]{ObjectOutputStream.class}, includeBaseClasses) != null;
    }

    public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
        try {
            Method readObjectMethod = getMethod(
                type, "writeObject", new Class[]{ObjectOutputStream.class}, false);
            readObjectMethod.invoke(instance, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call "
                + instance.getClass().getName()
                + ".writeObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call "
                + instance.getClass().getName()
                + ".writeObject()", e.getTargetException());
        }
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes,
        boolean includeBaseclasses) {
        Method method = getMethod(type, name, parameterTypes);
        return method == NO_METHOD
            || (!includeBaseclasses && !method.getDeclaringClass().equals(type))
            ? null
            : method;
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes) {
        if (type == null) {
            return null;
        }
        FastField method = new FastField(type, name);
        Method result = (Method)declaredCache.get(method);
        if (result == null) {
            try {
                result = type.getDeclaredMethod(name, parameterTypes);
                if (!result.isAccessible()) {
                    result.setAccessible(true);
                }
            } catch (NoSuchMethodException e) {
                result = getMethod(type.getSuperclass(), name, parameterTypes);
            }
            declaredCache.put(method, result);
        }
        return result;
    }

    private Method getRRMethod(final Class type, final String name) {
        final FastField method = new FastField(type, name);
        Method result = (Method)resRepCache.get(method);
        if (result == null) {
            result = getMethod(type, name, EMPTY_CLASSES, true);
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
            resRepCache.put(method, result);
        }
        return result == NO_METHOD ? null : result;
    }

    public void flushCache() {
        declaredCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
        resRepCache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
    }
}
