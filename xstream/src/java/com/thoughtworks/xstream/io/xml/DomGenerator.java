package com.thoughtworks.xstream.io.xml;

import java.util.List;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * A genrric interface for all {@link HierarchicalStreamWriter} implementations generating a DOM.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public interface DomGenerator extends HierarchicalStreamWriter {
    /**
     * Retrieve a List with the top elements. In the standrad use case this list will only contain a single element.
     * Additional elements can only occur, if {@link HierarchicalStreamWriter#startNode(String)} of the implementing
     * {@link HierarchicalStreamWriter} was called multiple times with an empty node stack. Such a situation occurs
     * calling {@link com.thoughtworks.xstream.XStream#marshal(Object, HierarchicalStreamWriter)} multiple times
     * directly.
     * 
     * @return a {@link List} with top nodes
     * @since upcoming
     */
    List getResult();
}
