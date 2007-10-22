package com.thoughtworks.xstream.converters;

import java.util.Iterator;

/**
 * To aid debugging, some components are passed an ErrorWriter
 * when things go wrong, allowing them to add information
 * to the error message that may be helpful to diagnose problems.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public interface ErrorWriter {

    /**
     * Add some information to the error message.
     *
     * @param name        something to identify the type of information (e.g. 'XPath').
     * @param information detail of the message (e.g. '/blah/moo[3]'
     */
    void add(String name, String information);

    /**
     * Retrieve information of the error message.
     * 
     * @param errorKey the key of the message
     * @return the value
     * @since upcoming
     */
    String get(String errorKey);

    /**
     * Retrieve an iterator over all keys of the error message.
     * 
     * @return an Iterator
     * @since upcoming
     */
    Iterator keys();
}
