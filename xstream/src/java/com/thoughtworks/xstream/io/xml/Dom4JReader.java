package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;

public class Dom4JReader extends AbstractDocumentReader {

    private Element currentElement;

    public Dom4JReader(Element rootElement) {
        super(rootElement);
    }

    public Dom4JReader(Document document) {
        this(document.getRootElement());
    }

    public String getNodeName() {
        return currentElement.getName();
    }

    public String getValue() {
        return currentElement.getText();
    }

    public String getAttribute(String name) {
        return currentElement.attributeValue(name);
    }

    public String getAttribute(int index) {
        return currentElement.attribute(index).getValue();
    }

    public int getAttributeCount() {
        return currentElement.attributeCount();
    }

    public String getAttributeName(int index) {
        return currentElement.attribute(index).getQualifiedName();
    }

    protected Object getParent() {
        return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.elements().get(index);
    }

    protected int getChildCount() {
        return currentElement.elements().size();
    }

    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("xpath", currentElement.getPath());
    }

}
