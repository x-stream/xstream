/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * Created on 14. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * A field dictionary instance caches information about classes fields.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class FieldDictionary implements Caching {

    private static final DictionaryEntry OBJECT_DICTIONARY_ENTRY = new DictionaryEntry(Collections.EMPTY_MAP,
        Collections.EMPTY_MAP);

    private transient Map dictionaryEntries;
    private transient FieldUtil fieldUtil;
    private final FieldKeySorter sorter;

    public FieldDictionary() {
        this(new ImmutableFieldKeySorter());
    }

    public FieldDictionary(final FieldKeySorter sorter) {
        this.sorter = sorter;
        init();
    }

    private void init() {
        dictionaryEntries = new HashMap();
        if (JVM.is15())
            try {
                fieldUtil = (FieldUtil)JVM.loadClassForName("com.thoughtworks.xstream.converters.reflection.FieldUtil15", true).newInstance();
            } catch (Exception e) {
                ;
            }
        if (fieldUtil == null)
            fieldUtil = new FieldUtil14();
    }

    /**
     * Returns an iterator for all fields for some class
     *
     * @param cls the class you are interested on
     * @return an iterator for its fields
     * @deprecated As of 1.3, use {@link #fieldsFor(Class)} instead
     */
    public Iterator serializableFieldsFor(final Class cls) {
        return fieldsFor(cls);
    }

    /**
     * Returns an iterator for all fields for some class
     *
     * @param cls the class you are interested on
     * @return an iterator for its fields
     */
    public Iterator fieldsFor(final Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    /**
     * Returns an specific field of some class. If definedIn is null, it searches for the field named 'name' inside the
     * class cls. If definedIn is different than null, tries to find the specified field name in the specified class cls
     * which should be defined in class definedIn (either equals cls or a one of it's superclasses)
     *
     * @param cls the class where the field is to be searched
     * @param name the field name
     * @param definedIn the superclass (or the class itself) of cls where the field was defined
     * @return the field itself
     * @throws ObjectAccessException if no field can be found
     */
    public Field field(final Class cls, final String name, final Class definedIn) {
        final Field field = fieldOrNull(cls, name, definedIn);
        if (field == null) {
            throw new MissingFieldException(cls.getName(), name);
        } else {
            return field;
        }
    }

    /**
     * Returns an specific field of some class. If definedIn is null, it searches for the field named 'name' inside the
     * class cls. If definedIn is different than null, tries to find the specified field name in the specified class cls
     * which should be defined in class definedIn (either equals cls or a one of it's superclasses)
     *
     * @param cls the class where the field is to be searched
     * @param name the field name
     * @param definedIn the superclass (or the class itself) of cls where the field was defined
     * @return the field itself or <code>null</code>
     * @since 1.4
     */
    public Field fieldOrNull(final Class cls, final String name, final Class definedIn) {
        final Map fields = buildMap(cls, definedIn != null);
        final Field field = (Field)fields.get(definedIn != null
            ? (Object)new FieldKey(name, definedIn, -1)
            : (Object)name);
        return field;
    }

    private Map buildMap(final Class type, final boolean tupleKeyed) {

        Class cls = type;

        DictionaryEntry lastDictionaryEntry = null;
        final LinkedList superClasses = new LinkedList();
        while (lastDictionaryEntry == null) {
            if (Object.class.equals(cls) || cls == null) {
                lastDictionaryEntry = OBJECT_DICTIONARY_ENTRY;
            } else {
                lastDictionaryEntry = getDictionaryEntry(cls);
            }
            if (lastDictionaryEntry == null) {
                superClasses.addFirst(cls);
                cls = cls.getSuperclass();
            }
        }

        for (final Iterator iter = superClasses.iterator(); iter.hasNext();) {
            cls = (Class)iter.next();
            DictionaryEntry newDictionaryEntry = buildDictionaryEntryForClass(cls, lastDictionaryEntry);
            synchronized (this) {
                final DictionaryEntry concurrentEntry = getDictionaryEntry(cls);
                if (concurrentEntry == null) {
                    dictionaryEntries.put(cls, newDictionaryEntry);
                } else {
                    newDictionaryEntry = concurrentEntry;
                }
            }
            lastDictionaryEntry = newDictionaryEntry;
        }

        return tupleKeyed ? lastDictionaryEntry.getKeyedByFieldKey() : lastDictionaryEntry.getKeyedByFieldName();

    }

    private DictionaryEntry buildDictionaryEntryForClass(final Class cls, final DictionaryEntry lastDictionaryEntry) {
        final Map keyedByFieldName = new HashMap(lastDictionaryEntry.getKeyedByFieldName());
        final Map keyedByFieldKey = new OrderRetainingMap(lastDictionaryEntry.getKeyedByFieldKey());
        final Field[] fields = cls.getDeclaredFields();
        if (JVM.reverseFieldDefinition()) {
            for (int i = fields.length >> 1; i-- > 0;) {
                final int idx = fields.length - i - 1;
                final Field field = fields[i];
                fields[i] = fields[idx];
                fields[idx] = field;
            }
        }
        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
            if (fieldUtil.isSynthetic(field) && field.getName().startsWith("$jacoco")) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            final FieldKey fieldKey = new FieldKey(field.getName(), field.getDeclaringClass(), i);
            final Field existent = (Field)keyedByFieldName.get(field.getName());
            if (existent == null
                    // do overwrite statics
                    || (existent.getModifiers() & Modifier.STATIC) != 0
                    // overwrite non-statics with non-statics only
                    || (existent != null && (field.getModifiers() & Modifier.STATIC) == 0)) {
                keyedByFieldName.put(field.getName(), field);
            }
            keyedByFieldKey.put(fieldKey, field);
        }
        final Map sortedFieldKeys = sorter.sort(cls, keyedByFieldKey);
        return new DictionaryEntry(keyedByFieldName, sortedFieldKeys);
    }

    private synchronized DictionaryEntry getDictionaryEntry(final Class cls) {
        return (DictionaryEntry)dictionaryEntries.get(cls);
    }

    public synchronized void flushCache() {
        dictionaryEntries.clear();
        if (sorter instanceof Caching) {
            ((Caching)sorter).flushCache();
        }
    }

    protected Object readResolve() {
        init();
        return this;
    }

    interface FieldUtil {
        boolean isSynthetic(Field field);
    }

    private static final class DictionaryEntry {

        private final Map keyedByFieldName;
        private final Map keyedByFieldKey;

        public DictionaryEntry(final Map keyedByFieldName, final Map keyedByFieldKey) {
            super();
            this.keyedByFieldName = keyedByFieldName;
            this.keyedByFieldKey = keyedByFieldKey;
        }

        public Map getKeyedByFieldName() {
            return keyedByFieldName;
        }

        public Map getKeyedByFieldKey() {
            return keyedByFieldKey;
        }
    }
}
