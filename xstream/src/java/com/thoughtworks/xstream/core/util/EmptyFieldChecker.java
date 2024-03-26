package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.mapper.ElementIgnoringMapper;


/**
 * Utility functions for {@link com.thoughtworks.xstream.annotations.XStreamExcludeEmpty} annotation
 * Primary usage is to check if corresponded field should be omitted
 * 
 * @author Ruslan Sibgatullin
 */
public final class EmptyFieldChecker {

    private static final transient Map<Class<?>, CheckIfEmpty> EMPTY_CHECKER_MAP = new HashMap<Class<?>, CheckIfEmpty>(){{
        put(String.class, new CheckIfEmpty<String>() {
            @Override
            public boolean isEmpty(String value) {
                return value == null || value.isEmpty();
            }
        });
        put(Collection.class, new CheckIfEmpty<Collection>() {
            @Override
            public boolean isEmpty(Collection value) {
                return value == null || value.isEmpty();
            }
        });
        put(Map.class, new CheckIfEmpty<Map>() {
            @Override
            public boolean isEmpty(Map value) {
                return value == null || value.isEmpty();
            }
        });
    }};

    public static void checkAndOmitIfEmpty(ElementIgnoringMapper elementIgnoringMapper, final Field field, Object item) {
        for (Class<?> assignableClass : EMPTY_CHECKER_MAP.keySet()) {
            omitIfEmpty(elementIgnoringMapper, field, item, assignableClass);
        }
    }

    private static void omitIfEmpty(ElementIgnoringMapper elementIgnoringMapper, Field field, Object item, Class<?> assignableClass) {
        if (assignableClass.isAssignableFrom(field.getType())) {
            try {
                field.setAccessible(true);
                final Object value = field.get(item);

                //noinspection unchecked
                if (EMPTY_CHECKER_MAP.get(assignableClass).isEmpty(value)) {
                    elementIgnoringMapper.omitField(field.getDeclaringClass(), field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new InitializationException("Field " + field.getName() + " cannot be accessed", e);
            }
        }
    }

    private interface CheckIfEmpty<T> {
        boolean isEmpty(T value);
    }
}
