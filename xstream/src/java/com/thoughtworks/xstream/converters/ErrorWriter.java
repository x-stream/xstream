package com.thoughtworks.xstream.converters;

/**
 * To aid debugging, some components are passed an ErrorWriter
 * when things go wrong, allowing them to add information
 * to the error message that may be helpful to diagnose problems.
 *
 * @author Joe Walnes
 */
public interface ErrorWriter {

    /**
     * Add some information to the error message.
     *
     * @param name        Something to identify the type of information (e.g. 'XPath').
     * @param information Detail of the message (e.g. '/blah/moo[3]'
     */
    void add(String name, String information);

}
