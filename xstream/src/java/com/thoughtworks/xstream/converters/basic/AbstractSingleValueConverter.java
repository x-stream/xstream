package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Base abstract implementation of  {@link com.thoughtworks.xstream.converters.SingleValueConverter}.
 * <p/>
 * <p>Subclasses should implement methods canConvert(Class) and fromString(String) for the conversion.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public abstract class AbstractSingleValueConverter implements SingleValueConverter {

    public abstract boolean canConvert(Class type);

    public String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    public abstract Object fromString(String str);

}
