package com.thoughtworks.xstream.converters.basic;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Converts a String to a String ;). Well ok, it doesn't
 * <i>actually</i> do any conversion.
 * <p>The converter uses a map to reuse instances. This map is by default a synchronized {@link WeakHashMap}.</p>
 *
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @see String#intern()
 */
public class StringConverter extends AbstractSingleValueConverter {

    /**
     * A Map to store strings as long as needed to map similar strings onto the same
     * instance and conserve memory. The map can be set from the outside during
     * construction, so it can be a lru map or a weak map, sychronised or not.
     */
    private final Map cache;

    public StringConverter(Map map) {
        this.cache = map;
    }

    public StringConverter() {
        this(Collections.synchronizedMap(new WeakHashMap()));
    }

    public boolean canConvert(Class type) {
        return type.equals(String.class);
    }

    public Object fromString(String str) {
        String s = (String)cache.get(str);

        if (s == null) {
            // fill cache
            cache.put(str, str);

            s = str;
        }

        return s;
    }

}
