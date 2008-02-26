/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * A field dictionary instance caches information about classes fields.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class FieldDictionary {

    private transient Map keyedByFieldNameCache;
    private transient Map keyedByFieldKeyCache;
    private final FieldKeySorter sorter;

    public FieldDictionary() {
        this(new ImmutableFieldKeySorter());
    }

    public FieldDictionary(FieldKeySorter sorter) {
        this.sorter = sorter;
        init();
    }

    private void init() {
        keyedByFieldNameCache = new WeakHashMap();
        keyedByFieldKeyCache = new WeakHashMap();
        keyedByFieldNameCache.put(Object.class, Collections.EMPTY_MAP);
        keyedByFieldKeyCache.put(Object.class, Collections.EMPTY_MAP);
    }

    /**
     * Returns an iterator for all fields for some class
     * 
     * @param cls the class you are interested on
     * @return an iterator for its fields
     * @deprecated since 1.3, use {@link #fieldsFor(Class)} instead
     */
    public Iterator serializableFieldsFor(Class cls) {
        return fieldsFor(cls);
    }

    /**
     * Returns an iterator for all fields for some class
     * 
     * @param cls the class you are interested on
     * @return an iterator for its fields
     */
    public Iterator fieldsFor(Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    /**
     * Returns an specific field of some class. If definedIn is null, it searches for the field
     * named 'name' inside the class cls. If definedIn is different than null, tries to find the
     * specified field name in the specified class cls which should be defined in class
     * definedIn (either equals cls or a one of it's superclasses)
     * 
     * @param cls the class where the field is to be searched
     * @param name the field name
     * @param definedIn the superclass (or the class itself) of cls where the field was defined
     * @return the field itself
     */
    public Field field(Class cls, String name, Class definedIn) {
        Map fields = buildMap(cls, definedIn != null);
        Field field = (Field)fields.get(definedIn != null ? (Object)new FieldKey(
            name, definedIn, 0) : (Object)name);
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls.getName() + "." + name);
        } else {
            return field;
        }
    }

    private Map buildMap(final Class type, boolean tupleKeyed) {
        Class cls = type;
        synchronized (this) {
            if (!keyedByFieldNameCache.containsKey(type)) {
                final List superClasses = new ArrayList();
                while (!Object.class.equals(cls)) {
                    superClasses.add(0, cls);
                    cls = cls.getSuperclass();
                }
                Map lastKeyedByFieldName = Collections.EMPTY_MAP;
                Map lastKeyedByFieldKey = Collections.EMPTY_MAP;
                for (final Iterator iter = superClasses.iterator(); iter.hasNext();) {
                    cls = (Class)iter.next();
                    if (!keyedByFieldNameCache.containsKey(cls)) {
                        final Map keyedByFieldName = new HashMap(lastKeyedByFieldName);
                        final Map keyedByFieldKey = new OrderRetainingMap(lastKeyedByFieldKey);
                        Field[] fields = cls.getDeclaredFields();
                        if (JVM.reverseFieldDefinition()) {
                            for (int i = fields.length >> 1; i-- > 0;) {
                                final int idx = fields.length - i - 1;
                                final Field field = fields[i];
                                fields[i] = fields[idx];
                                fields[idx] = field;
                            }
                        }
                        for (int i = 0; i < fields.length; i++) {
                            Field field = fields[i];
                            FieldKey fieldKey = new FieldKey(field.getName(), field
                                .getDeclaringClass(), i);
                            field.setAccessible(true);
                            Field existent = (Field)keyedByFieldName.get(field.getName());
                            if (existent == null
                            // do overwrite statics
                                || ((existent.getModifiers() & Modifier.STATIC) != 0)
                                // overwrite non-statics with non-statics only
                                || (existent != null && ((field.getModifiers() & Modifier.STATIC) == 0))) {
                                keyedByFieldName.put(field.getName(), field);
                            }
                            keyedByFieldKey.put(fieldKey, field);
                        }
                        keyedByFieldNameCache.put(cls, keyedByFieldName);
                        keyedByFieldKeyCache.put(cls, sorter.sort(type, keyedByFieldKey));
                    }
                    lastKeyedByFieldName = (Map)keyedByFieldNameCache.get(cls);
                    lastKeyedByFieldKey = (Map)keyedByFieldKeyCache.get(cls);
                }
            }
        }
        return (Map)(tupleKeyed ? keyedByFieldKeyCache.get(type) : keyedByFieldNameCache
            .get(type));
    }

    protected Object readResolve() {
        init();
        return this;
    }

}
