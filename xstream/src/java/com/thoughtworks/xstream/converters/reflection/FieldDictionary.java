package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * A field dictionary instance caches information about classes fields.
 */
public class FieldDictionary {

    private final Map keyedByFieldNameCache = new WeakHashMap();
    private final Map keyedByFieldKeyCache = new WeakHashMap();
    private final FieldKeySorter sorter;

    public FieldDictionary() {
        this(new ImmutableFieldKeySorter());
    }

    public FieldDictionary(FieldKeySorter sorter) {
        this.sorter = sorter;
    }

    /**
     * Returns an iterator for all serializable fields for some class
     * 
     * @param cls the class you are interested on
     * @return an iterator for its serializable fields
     */
    public Iterator serializableFieldsFor(Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    /**
     * Returns an specific field of some class. If definedIn is null, it searchs for the field
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
        synchronized (keyedByFieldNameCache) {
            synchronized (keyedByFieldKeyCache) {
                if (!keyedByFieldNameCache.containsKey(type)) {
                    final Map keyedByFieldName = new HashMap();
                    final Map keyedByFieldKey = new OrderRetainingMap();
                    while (!Object.class.equals(cls)) {
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
                            // do overwrite statics, but with non-statics only
                                || ((existent.getModifiers() & Modifier.STATIC) != 0)
                                && ((field.getModifiers() & Modifier.STATIC) == 0)) {
                                keyedByFieldName.put(field.getName(), field);
                            }
                            keyedByFieldKey.put(fieldKey, field);
                        }
                        cls = cls.getSuperclass();
                    }
                    keyedByFieldNameCache.put(type, keyedByFieldName);
                    keyedByFieldKeyCache.put(type, sorter.sort(type, keyedByFieldKey));
                }
            }
        }
        return (Map)(tupleKeyed ? keyedByFieldKeyCache.get(type) : keyedByFieldNameCache
            .get(type));
    }

}
