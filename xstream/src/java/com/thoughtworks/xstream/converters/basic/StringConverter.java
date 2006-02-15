package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a String to a String ;). Well ok, it doesn't
 * <i>actually</i> do any conversion.
 * <p>The converter always calls intern() on the returned
 * String to encourage the JVM to reuse instances.</p>
 *
 * @author Joe Walnes
 * @see String#intern()
 */
public class StringConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(String.class);
    }

    public Object fromString(String str) {
        return str.intern();
    }

}
