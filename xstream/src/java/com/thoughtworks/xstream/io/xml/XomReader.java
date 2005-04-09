package com.thoughtworks.xstream.io.xml;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;

public class XomReader extends AbstractTreeReader {

    private Element currentElement;

    public XomReader(Element rootElement) {
        super(rootElement);
    }

    public XomReader(Document document) {
        super(document.getRootElement());
    }

    public String getNodeName() {
        return currentElement.getLocalName();
    }

    public String getValue() {
        return currentElement.getValue();
    }

    public String getAttribute(String name) {
        return currentElement.getAttributeValue(name);
    }

    public String getAttribute(int index) {
        return currentElement.getAttribute(index).getValue();
    }

    public int getAttributeCount() {
        return currentElement.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return currentElement.getAttribute(index).getQualifiedName();
    }

    protected int getChildCount() {
        return currentElement.getChildElements().size();
    }

    protected Object getParent() {
        return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.getChildElements().get(index);
    }

    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }
}
