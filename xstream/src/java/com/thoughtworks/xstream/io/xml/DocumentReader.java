package com.thoughtworks.xstream.io.xml;

import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * A generic interface for all {@link HierarchicalStreamReader} implementations reading a DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public interface DocumentReader extends HierarchicalStreamReader {

    /**
     * Retrieve the current element. In the standrad use case this list will only contain a single element.
     * Additional elements can only occur, if {@link HierarchicalStreamWriter#startNode(String)} of the implementing
     * {@link HierarchicalStreamWriter} was called multiple times with an empty node stack. Such a situation occurs
     * calling {@link com.thoughtworks.xstream.XStream#marshal(Object, HierarchicalStreamWriter)} multiple times
     * directly.
     * 
     * @return a {@link List} with top nodes
     * @since upcoming
     */
    public Object getCurrent();
}
