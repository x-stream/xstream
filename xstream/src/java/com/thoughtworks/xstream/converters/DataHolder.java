package com.thoughtworks.xstream.converters;

import java.util.Iterator;

/**
 * Holds generic data, to be used as seen fit by the user.
 *
 * @author Joe Walnes
 */
public interface DataHolder {

    Object get(Object key);
    void put(Object key, Object value);
    Iterator keys();

}
