/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 */
public class SerializationMethodInvoker {

    private Map cache = Collections.synchronizedMap(new HashMap());
    private static final Object NO_METHOD = new Object();
    private static final Object[] EMPTY_ARGS = new Object[0];

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
                    throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()", e.getTargetException());
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
                    Object[] EMPTY_ARGS = new Object[0];
                    return writeReplaceMethod.invoke(object, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call " + object.getClass().getName() + ".writeReplace()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call " + object.getClass().getName() + ".writeReplace()", e.getTargetException());
                }
            } else {
                return object;
            }
        }
    }

    public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
        return getMethod(type, "readObject", new Class[]{ObjectInputStream.class}, includeBaseClasses) != null;
    }

    public void callReadObject(Class type, Object object, ObjectInputStream stream) {
        try {
            Method readObjectMethod = getMethod(type, "readObject", new Class[]{ObjectInputStream.class}, false);
            readObjectMethod.invoke(object, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e.getTargetException());
        }
    }

    public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
        return getMethod(type, "writeObject", new Class[]{ObjectOutputStream.class}, includeBaseClasses) != null;
    }

    public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
        try {
            Method readObjectMethod = getMethod(type, "writeObject", new Class[]{ObjectOutputStream.class}, false);
            readObjectMethod.invoke(instance, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call " + instance.getClass().getName() + ".writeObject()", e.getTargetException());
        }
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes, boolean includeBaseclasses) {
        Object key = type.getName() + "." + name + "." + includeBaseclasses;
        if (cache.containsKey(key)) {
            Object result = cache.get(key);
            return (Method) (result == NO_METHOD ? null : result);
        }
        if (includeBaseclasses) {
            while (type != null) {
                try {
                    Method result = type.getDeclaredMethod(name, parameterTypes);
                    result.setAccessible(true);
                    cache.put(key, result);
                    return result;
                } catch (NoSuchMethodException e) {
                    type = type.getSuperclass();
                }
            }
            cache.put(key, NO_METHOD);
            return null;
        } else {
            try {
                Method result = type.getDeclaredMethod(name, parameterTypes);
                result.setAccessible(true);
                cache.put(key, result);
                return result;
            } catch (NoSuchMethodException e) {
                cache.put(key, NO_METHOD);
                return null;
            }
        }
    }

}
