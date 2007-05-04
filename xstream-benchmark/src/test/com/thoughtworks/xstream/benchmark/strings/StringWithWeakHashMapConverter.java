package com.thoughtworks.xstream.benchmark.strings;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Uses WeakHashMap for StringConverter.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 */
public class StringWithWeakHashMapConverter implements Product {

    private final XStream xstream;

    public StringWithWeakHashMapConverter() {
        xstream = new XStream(new XppDriver());
        xstream.registerConverter(new StringConverter());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "StringConverter using WeakHashMap";
    }

    /**
     * Converts a String to a String. Well ok, it doesn't <i>actually</i> do any conversion.
     * <p>
     * The converter always uses an external map to reduce the string occurences, because
     * String.intern() is using the PermGenSpace and wasting it. Additionally the call to
     * intern() is a native call and costs performance.
     * </p>
     * 
     * @author Rene Schwietzke
     * @see WeakHashMap
     */
    public static class StringConverter extends AbstractSingleValueConverter {
        /**
         * A Map to store strings as long as needed to map similar strings onto the same
         * instance and conserve memory. The map can be set from the outside during
         * construction, so it can be a lru map or a weak map, sychronised or not.
         */
        private final Map cache;

        public StringConverter() {
            this.cache = new WeakHashMap();
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
}
