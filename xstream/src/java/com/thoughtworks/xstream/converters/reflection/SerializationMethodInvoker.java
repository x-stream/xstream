package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 */
public class SerializationMethodInvoker {

    private Map cache = Collections.synchronizedMap(new HashMap());
    private static final Object NO_METHOD = new Object();

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     */
    public Object callReadResolve(Object result) {
        if (result == null) {
            return null;
        } else {
            Method readResolveMethod = getMethod(result.getClass(), "readResolve", null);
            if (readResolveMethod != null) {
                try {
                    return readResolveMethod.invoke(result, null);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()", e);
                } catch (InvocationTargetException e) {
                    throw new ObjectAccessException("Could not call " + result.getClass().getName() + ".readResolve()", e);
                }
            } else {
                return result;
            }
        }
    }

    public boolean supportsReadObject(Class type) {
        return getMethod(type, "readObject", new Class[]{ObjectInputStream.class}) != null;
    }

    public void callReadObject(Object object, ObjectInputStream stream) {
        try {
            Method readObjectMethod = getMethod(object.getClass(), "readObject", new Class[]{ObjectInputStream.class});
            readObjectMethod.invoke(object, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        }
    }

    public boolean supportsWriteObject(Class type) {
        return getMethod(type, "writeObject", new Class[]{ObjectOutputStream.class}) != null;
    }

    public void callWriteObject(Object object, ObjectOutputStream stream) {
        try {
            Method readObjectMethod = getMethod(object.getClass(), "writeObject", new Class[]{ObjectOutputStream.class});
            readObjectMethod.invoke(object, new Object[]{stream});
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        } catch (InvocationTargetException e) {
            throw new ConversionException("Could not call " + object.getClass().getName() + ".readObject()", e);
        }
    }

    private Method getMethod(Class type, String name, Class[] parameterTypes) {
        Object key = type.getName() + "." + name;
        if (cache.containsKey(key)) {
            Object result = cache.get(key);
            return (Method) (result == NO_METHOD ? null : result);
        }
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
    }

}
