package com.thoughtworks.xstream.io.xml;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Laurent Bihanic
 */
public class JDomReader extends AbstractDocumentReader {

    private Element currentElement;

    public JDomReader(Element root) {
        super(root);
    }

    public JDomReader(Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.2
     */
    public JDomReader(Element root, XmlFriendlyReplacer replacer) {
        super(root, replacer);
    }

    /**
     * @since 1.2
     */
    public JDomReader(Document document, XmlFriendlyReplacer replacer) {
        super(document.getRootElement(), replacer);
    }
    
    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }

    protected Object getParent() {
        // JDOM 1.0:
        return currentElement.getParentElement();

        // JDOM b10:
        // Parent parent = currentElement.getParent();
        // return (parent instanceof Element) ? (Element)parent : null;

        // JDOM b9 and earlier:
        // return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.getChildren().get(index);
    }

    protected int getChildCount() {
        return currentElement.getChildren().size();
    }

    public String getNodeName() {
        return unescapeXmlName(currentElement.getName());
    }

    public String getValue() {
        return currentElement.getText();
    }

    public String getAttribute(String name) {
        return currentElement.getAttributeValue(name);
    }

    public String getAttribute(int index) {
        return ((Attribute) currentElement.getAttributes().get(index)).getValue();
    }

    public int getAttributeCount() {
        return currentElement.getAttributes().size();
    }

    public String getAttributeName(int index) {
        return unescapeXmlName(((Attribute) currentElement.getAttributes().get(index)).getQualifiedName());
    }

}

