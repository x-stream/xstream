/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. November 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.StreamException;

import java.io.File;


/**
 * PersistenceStrategy to assign keys with single value to objects persisted in files. The
 * default naming strategy is based on the key's toString method and escapes non digit, non a-z,
 * A-Z characters. In order to change the escaping/unescaping algorithm, simply extend this
 * class and rewrite its getName/extractKey methods. Note, that this implementation silently
 * implies that the keys actually are Strings, since the keys will be turned into keys at
 * deserialization time.
 * 
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 * @since upcoming
 */
public class FilePersistenceStrategy extends AbstractFilePersistenceStrategy {

    private final String illegalChars;

    public FilePersistenceStrategy(final File baseDirectory) {
        this(baseDirectory, new XStream());
    }

    public FilePersistenceStrategy(final File baseDirectory, final XStream xstream) {
        this(baseDirectory, xstream, "utf-8", "<>?:/\\\"|*%");
    }

    public FilePersistenceStrategy(
        final File baseDirectory, final XStream xstream, final String encoding,
        final String illegalChars) {
        super(baseDirectory, xstream, encoding);
        this.illegalChars = illegalChars;
    }

    protected boolean isValid(final File dir, final String name) {
        return super.isValid(dir, name) && name.indexOf('@') > 0;
    }

    /**
     * Given a filename, the unescape method returns the key which originated it.
     * 
     * @param name the filename
     * @return the original key
     */
    protected Object extractKey(final String name) {
        final String key = unescape(name.substring(0, name.length() - 4));
        if ("null@null".equals(key)) {
            return null;
        }
        int idx = key.indexOf('@');
        if (idx < 0) {
            throw new StreamException("Not a valid key: " + key);
        }
        Class type = getMapper().realClass(key.substring(0, idx));
        Converter converter = getConverterLookup().lookupConverterForType(type);
        if (converter instanceof SingleValueConverter) {
            final SingleValueConverter svConverter = (SingleValueConverter)converter;
            return svConverter.fromString(key.substring(idx + 1));
        } else {
            throw new StreamException("No SingleValueConverter for type "
                + type.getName()
                + " available");
        }
    }

    protected String unescape(String name) {
        final StringBuffer buffer = new StringBuffer();
        for (int idx = name.indexOf('%'); idx >= 0; idx = name.indexOf('%')) {
            buffer.append(name.substring(0, idx));
            int c = Integer.parseInt(name.substring(idx + 1, idx + 3), 16);
            buffer.append((char)c);
            name = name.substring(idx + 3);
        }
        buffer.append(name);
        return buffer.toString();
    }

    /**
     * Given a key, the escape method returns the filename which shall be used.
     * 
     * @param key the key
     * @return the desired and escaped filename
     */
    protected String getName(final Object key) {
        if (key == null) {
            return "null@null.xml";
        }
        Class type = key.getClass();
        Converter converter = getConverterLookup().lookupConverterForType(type);
        if (converter instanceof SingleValueConverter) {
            final SingleValueConverter svConverter = (SingleValueConverter)converter;
            return getMapper().serializedClass(type)
                + '@'
                + escape(svConverter.toString(key))
                + ".xml";
        } else {
            throw new StreamException("No SingleValueConverter for type "
                + type.getName()
                + " available");
        }
    }

    protected String escape(final String key) {
        final StringBuffer buffer = new StringBuffer();
        final char[] array = key.toCharArray();
        for (int i = 0; i < array.length; i++ ) {
            final char c = array[i];
            if (c >= ' ' && illegalChars.indexOf(c) < 0) {
                buffer.append(c);
            } else {
                buffer.append("%" + Integer.toHexString(c).toUpperCase());
            }
        }
        return buffer.toString();
    }
}
