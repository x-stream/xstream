package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.LinkedList;

public class MarshallingContextAdaptor implements MarshallingContext {

    private XMLWriter xmlWriter;
    private ConverterLookup converterLookup;

    private LinkedList stack = new LinkedList();

    public MarshallingContextAdaptor(Object root, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        stack.add(root);
        this.xmlWriter = xmlWriter;
        this.converterLookup = converterLookup;
    }

    public void xmlWriteText(String text) {
        xmlWriter.writeText(text);
    }

    public void xmlStartElement(String fieldName) {
        xmlWriter.startElement(fieldName);
    }

    public void xmlEndElement() {
        xmlWriter.endElement();
    }

    public void xmlAddAttribute(String name, String value) {
        xmlWriter.addAttribute(name, value);
    }

    public void convert(Object item) {
        stack.addLast(item);
        Converter converter = converterLookup.lookupConverterForType(item.getClass());
        converter.toXML(this);
        stack.removeLast();
    }

    public Object currentObject() {
        return stack.getLast();
    }

}
