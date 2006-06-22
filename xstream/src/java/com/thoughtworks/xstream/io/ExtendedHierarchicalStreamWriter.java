package com.thoughtworks.xstream.io;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: Jun 15, 2006
 * Time: 1:49:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExtendedHierarchicalStreamWriter extends HierarchicalStreamWriter {

    void startNode(String name, Class clazz);    

}
