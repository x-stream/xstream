package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author J. Matthew Prior
 */
public class URLConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(URL.class);
    }

    protected Object fromString(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

}
