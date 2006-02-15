package com.thoughtworks.xstream.converters.basic;

/**
 * Converts the contents of a StringBuffer to XML.
 *
 * @author Joe Walnes
 */
public class StringBufferConverter extends AbstractSingleValueConverter {

    public Object fromString(String str) {
        return new StringBuffer(str);
    }

    public boolean canConvert(Class type) {
        return type.equals(StringBuffer.class);
    }
}
