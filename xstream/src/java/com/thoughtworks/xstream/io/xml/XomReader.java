package com.thoughtworks.xstream.io.xml;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

public class XomReader extends AbstractDocumentReader {

    private Element currentElement;

    public XomReader(Element rootElement) {
        super(rootElement);
    }

    public XomReader(Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.2
     */
    public XomReader(Element rootElement, XmlFriendlyReplacer replacer) {
        super(rootElement, replacer);
    }

    /**
     * @since 1.2
     */
    public XomReader(Document document, XmlFriendlyReplacer replacer) {
        super(document.getRootElement(), replacer);
    }
    
    public String getNodeName() {
        return unescapeXmlName(currentElement.getLocalName());
    }

    public String getValue() {
        // currentElement.getValue() not used as this includes text of child elements, which we don't want.
        StringBuffer result = new StringBuffer();
        int childCount = currentElement.getChildCount();
        for(int i = 0; i < childCount; i++) {
            Node child = currentElement.getChild(i);
            if (child instanceof Text) {
                Text text = (Text) child;
                result.append(text.getValue());
            }
        }
        return result.toString();
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
        return unescapeXmlName(currentElement.getAttribute(index).getQualifiedName());
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
