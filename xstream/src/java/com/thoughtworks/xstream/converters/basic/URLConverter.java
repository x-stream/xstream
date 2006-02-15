package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Converts a java.net.URL to a string.
 *
 * @author J. Matthew Pryor
 */
public class URLConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(URL.class);
    }

    public Object fromString(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new ConversionException(e);
        }
    }

}
