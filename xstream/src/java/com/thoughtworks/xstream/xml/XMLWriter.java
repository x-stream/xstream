package com.thoughtworks.xstream.xml;

public interface XMLWriter {

    void startElement(String name);

    void addAttribute(String key, String value);

    void writeText(String text);

    void endElement();

}
