package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


/**
 * A generic interface for all {@link HierarchicalStreamReader} implementations reading a DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public interface DocumentReader extends HierarchicalStreamReader {

    /**
     * Retrieve the current processed node of the DOM.
     * 
     * @return the current node
     * @since 1.2.1
     */
    public Object getCurrent();
}
