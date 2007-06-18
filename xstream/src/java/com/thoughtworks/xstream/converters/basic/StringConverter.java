package com.thoughtworks.xstream.converters.basic;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Converts a String to a String ;).
 * <p>
 * Well ok, it doesn't <i>actually</i> do any conversion. The converter uses a map to reuse instances. This map is by
 * default a synchronized {@link WeakHashMap}.
 * </p>
 *
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @author J&ouml;rg Schaible
 * @see String#intern()
 */
public class StringConverter extends AbstractSingleValueConverter {

    /**
     * A Map to store strings as long as needed to map similar strings onto the same instance and conserve memory. The
     * map can be set from the outside during construction, so it can be a LRU map or a weak map, sychronised or not.
     */
    private final Map cache;

    public StringConverter(final Map map) {
        cache = map;
    }

    public StringConverter() {
        this(Collections.synchronizedMap(new WeakHashMap()));
    }

    public boolean canConvert(final Class type) {
        return type.equals(String.class);
    }

    public Object fromString(final String str) {
        final WeakReference ref = (WeakReference)cache.get(str);
        String s = (String)(ref == null ? null : ref.get());

        if (s == null) {
            // fill cache
            cache.put(str, new WeakReference(str));

            s = str;
        }

        return s;
    }
}
