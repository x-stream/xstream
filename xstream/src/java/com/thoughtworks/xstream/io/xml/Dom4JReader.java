package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;

import org.dom4j.Document;
import org.dom4j.Element;

public class Dom4JReader extends AbstractDocumentReader {

    private Element currentElement;

    public Dom4JReader(Element rootElement) {
        this(rootElement, new XmlFriendlyReplacer());
    }

    public Dom4JReader(Document document) {
        this(document.getRootElement());
    }

    /**
     * @since 1.2
     */
    public Dom4JReader(Element rootElement, XmlFriendlyReplacer replacer) {
        super(rootElement, replacer);
    }

    /**
     * @since 1.2
     */
    public Dom4JReader(Document document, XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), replacer);
    }
    
    public String getNodeName() {
        return unescapeXmlName(currentElement.getName());
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
        return unescapeXmlName(currentElement.attribute(index).getQualifiedName());
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
