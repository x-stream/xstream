package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Resolves an object as native serialization does by calling readResolve(), if available.
 *
 * @author Joe Walnes
 */
public class InstanceResolver {

    private Map cache = Collections.synchronizedMap(new HashMap());
    private static final Object NO_METHOD = new Object();

    public Object resolve(Object result) {
        if (result == null) {
            return null;
        } else {
            Method readResolveMethod = findReadResolveMethod(result.getClass());
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

    private Method findReadResolveMethod(Class type) {
        if (cache.containsKey(type)) {
            Object result = cache.get(type);
            return (Method) (result == NO_METHOD ? null : result);
        }
        while (type != null) {
            try {
                Method result = type.getDeclaredMethod("readResolve", null);
                result.setAccessible(true);
                cache.put(type, result);
                return result;
            } catch (NoSuchMethodException e) {
                type = type.getSuperclass();
            }
        }
        cache.put(type, NO_METHOD);
        return null;
    }

}
