package com.thoughtworks.xstream.io;

public interface HierarchicalStreamWriter {

    void startElement(String name);

    void addAttribute(String key, String value);

    void writeText(String text);

    void endElement();

}
