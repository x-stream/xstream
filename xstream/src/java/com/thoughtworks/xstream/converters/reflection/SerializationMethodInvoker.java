/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011 XStream Committers.
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
    private static final FastField[] OBJECT_TYPE_FIELDS = new FastField[]{
        new FastField(Object.class, "readResolve"), 
        new FastField(Object.class, "writeReplace"), 
        new FastField(Object.class, "readObject"), 
        new FastField(Object.class, "writeObject")
    };
    private Map cache = Collections.synchronizedMap(new HashMap());
    {
        for(int i = 0; i < OBJECT_TYPE_FIELDS.length; ++i) {
            cache.put(OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
    }

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     */
    public Object callReadResolve(Object result) {
        if (result == null) {
            return null;
        } else {
            Method readResolveMethod = getMethod(result.getClass(), "readResolve", null, true);
            if (readResolveMethod != null) {
                try {
                    return readResolveMethod.invoke(result, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call "
                        + result.getClass().getName()
                        + ".readResolve()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call "
                        + result.getClass().getName()
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
            Method writeReplaceMethod = getMethod(object.getClass(), "writeReplace", null, true);
            if (writeReplaceMethod != null) {
                try {
                    return writeReplaceMethod.invoke(object, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call "
                        + object.getClass().getName()
                        + ".writeReplace()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call "
                        + object.getClass().getName()
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
        FastField method = new FastField(type, name);
        Method result = (Method)cache.get(method);

        if (result == null) {
            try {
                result = type.getDeclaredMethod(name, parameterTypes);
                if (!result.isAccessible()) {
                    result.setAccessible(true);
                }
            } catch (NoSuchMethodException e) {
                result = getMethod(type.getSuperclass(), name, parameterTypes);
            }
            cache.put(method, result);
        }
        return result;
    }

    public void flushCache() {
        cache.keySet().retainAll(Arrays.asList(OBJECT_TYPE_FIELDS));
    }
}
