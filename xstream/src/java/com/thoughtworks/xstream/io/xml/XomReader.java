package com.thoughtworks.xstream.io.xml;

import nu.xom.Document;
import nu.xom.Element;

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
