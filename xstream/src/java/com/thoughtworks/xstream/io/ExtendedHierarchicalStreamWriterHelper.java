package com.thoughtworks.xstream.io;

public class ExtendedHierarchicalStreamWriterHelper {
    public static void startNode(HierarchicalStreamWriter writer, String name, Class clazz) {
        if (writer instanceof ExtendedHierarchicalStreamWriter) {
            ((ExtendedHierarchicalStreamWriter) writer).startNode(name, clazz);
        } else {
            writer.startNode(name);
        }
    }
}
