/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2014, 2015, 2016, 2021, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. February 2015 by Joerg Schaible, copied from c.t.x.converters.reflection.SerializationMemberInvoker.
 */
package com.thoughtworks.xstream.core.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.Caching;


/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class SerializationMembers implements Caching {

    private static final class NO_METHOD_MARKER {
        private void noMethod() {
        }
    }
    private static final Method NO_METHOD = NO_METHOD_MARKER.class.getDeclaredMethods()[0];
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class[] EMPTY_CLASSES = new Class[0];
    private static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
    private static final int PERSISTENT_FIELDS_MODIFIER = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
    private static final String[] OBJECT_TYPE_FIELDS = {"readResolve", "writeReplace", "readObject", "writeObject"};
    private final MemberStore declaredCache = MemberStore.newSynchronizedInstance();
    private final MemberStore resRepCache = MemberStore.newSynchronizedInstance();
    private final Map fieldCache = Collections.synchronizedMap(new HashMap());
    {
        for(int i = 0; i < OBJECT_TYPE_FIELDS.length; ++i) {
            declaredCache.put(Object.class, OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
        for(int i = 0; i < 2; ++i) {
            resRepCache.put(Object.class, OBJECT_TYPE_FIELDS[i], NO_METHOD);
        }
    }

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     */
    public Object callReadResolve(final Object result) {
        if (result == null) {
            return null;
        } else {
            final Class resultType = result.getClass();
            final Method readResolveMethod = getRRMethod(resultType, "readResolve");
            if (readResolveMethod != null) {
                ErrorWritingException ex = null;
                try {
                    return readResolveMethod.invoke(result, EMPTY_ARGS);
                } catch (IllegalAccessException e) {
                    ex = new ObjectAccessException("Cannot access method", e);
                } catch (InvocationTargetException e) {
                    ex = new ConversionException("Failed calling method", e.getTargetException());
                }
                ex.add("method", resultType.getName() + ".readResolve()");
                throw ex;
            } else {
                return result;
            }
        }
    }

    public Object callWriteReplace(final Object object) {
        if (object == null) {
            return null;
        } else {
            final Class objectType = object.getClass();
            final Method writeReplaceMethod = getRRMethod(objectType, "writeReplace");
            if (writeReplaceMethod != null) {
                ErrorWritingException ex = null;
                try {
                    Object replaced = writeReplaceMethod.invoke(object, EMPTY_ARGS);
                    if (replaced != null && !object.getClass().equals(replaced.getClass())) {
                        // call further writeReplace methods on replaced
                        replaced = callWriteReplace(replaced);
                    }
                    return replaced;
                } catch (final IllegalAccessException e) {
                    ex = new ObjectAccessException("Cannot access method", e);
                } catch (InvocationTargetException e) {
                    ex = new ConversionException("Failed calling method", e.getTargetException());
                } catch (final ErrorWritingException e) {
                    ex = e;
                }
                ex.add("method", objectType.getName() + ".writeReplace()");
                throw ex;
            } else {
                return object;
            }
        }
    }

    public boolean supportsReadObject(final Class type, final boolean includeBaseClasses) {
        return getMethod(
            type, "readObject", new Class[]{ObjectInputStream.class}, includeBaseClasses) != null;
    }

    public void callReadObject(final Class type, final Object object, final ObjectInputStream stream) {
        ErrorWritingException ex = null;
        try {
            Method readObjectMethod = getMethod(
                type, "readObject", new Class[]{ObjectInputStream.class}, false);
            readObjectMethod.invoke(object, new Object[]{stream});
        } catch (IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot access method", e);
        } catch (InvocationTargetException e) {
            ex = new ConversionException("Failed calling method", e.getTargetException());
        }
        if (ex != null) {
            ex.add("method", object.getClass().getName() + ".readObject()");
            throw ex;
        }
    }

    public boolean supportsWriteObject(final Class type, final boolean includeBaseClasses) {
        return getMethod(
            type, "writeObject", new Class[]{ObjectOutputStream.class}, includeBaseClasses) != null;
    }

    public void callWriteObject(final Class type, final Object instance, final ObjectOutputStream stream) {
        ErrorWritingException ex = null;
        try {
            Method readObjectMethod = getMethod(
                type, "writeObject", new Class[]{ObjectOutputStream.class}, false);
            readObjectMethod.invoke(instance, new Object[]{stream});
        } catch (IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot access method", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof ConversionException)
                    throw (ConversionException)cause;
            ex = new ConversionException("Failed calling method", e.getTargetException());
        }
        if (ex != null) {
            ex.add("method", instance.getClass().getName() + ".writeObject()");
            throw ex;
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
            Method result = (Method)declaredCache.get(type, name);
            if (result == null) {
                try {
                    result = type.getDeclaredMethod(name, parameterTypes);
                    if (!result.isAccessible()) {
                        result.setAccessible(true);
                    }
                } catch (NoSuchMethodException e) {
                    result = getMethod(type.getSuperclass(), name, parameterTypes);
                }
                declaredCache.put(type, name, result);
            }
            return result;
        }

        private Method getRRMethod(final Class type, final String name) {
            Method result = (Method)resRepCache.get(type, name);
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
                resRepCache.put(type, name, result);
            }
            return result == NO_METHOD ? null : result;
        }

        public boolean hasSerializablePersistentFields(final Class type) {
            if (type == null) {
                return false;
            }
            ObjectStreamField[] result = (ObjectStreamField[])fieldCache.get(type.getName());
            if (result == null) {
                ErrorWritingException ex = null;
                try {
                    final Field field = type.getDeclaredField("serialPersistentFields");
                    if ((field.getModifiers() & PERSISTENT_FIELDS_MODIFIER) == PERSISTENT_FIELDS_MODIFIER) {
                        field.setAccessible(true);
                        result = (ObjectStreamField[])field.get(null);
                    }
                } catch (final NoSuchFieldException e) {
                } catch (final IllegalAccessException e) {
                    ex = new ObjectAccessException("Cannot get field", e);
                } catch (final ClassCastException e) {
                    ex = new ConversionException("Incompatible field type", e);
                }
                if (ex != null) {
                    ex.add("field", type.getName() + ".serialPersistentFields");
                    throw ex;
                }
                if (result == null) {
                    result = NO_FIELDS;
                }
                fieldCache.put(type.getName(), result);
            }
            return result != NO_FIELDS;
        }

    public void flushCache() {
        final Set classNames = Collections.singleton(Object.class.getName());
        declaredCache.keySet().retainAll(classNames);
        resRepCache.keySet().retainAll(classNames);
    }
}
