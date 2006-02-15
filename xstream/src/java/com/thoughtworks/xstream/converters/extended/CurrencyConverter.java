package com.thoughtworks.xstream.converters.extended;

import java.util.Currency;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.util.Currency to String. Despite the name of this class, it has nothing to do with converting
 * currencies between exchange rates! It makes sense in the context of XStream.
 *
 * @author Jose A. Illescas 
 * @author Joe Walnes
 */
public class CurrencyConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Currency.class);
    }

    public Object fromString(String str) {
        return Currency.getInstance(str);
    }

}
