package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XppDomReader extends AbstractDocumentReader {

    private Xpp3Dom currentElement;

    public XppDomReader(Xpp3Dom xpp3Dom) {
        super(xpp3Dom);
    }

    /**
     * @since 1.2
     */
    public XppDomReader(Xpp3Dom xpp3Dom, XmlFriendlyReplacer replacer) {
        super(xpp3Dom, replacer);
    }
    
    public String getNodeName() {
        return unescapeXmlName(currentElement.getName());
    }

    public String getValue() {
        String text = null;

        try {
            text = currentElement.getValue();
        } catch (Exception e) {
            // do nothing.
        }

        return text == null ? "" : text;
    }

    public String getAttribute(String attributeName) {
        return currentElement.getAttribute(attributeName);
    }

    public String getAttribute(int index) {
        return currentElement.getAttribute(currentElement.getAttributeNames()[index]);
    }

    public int getAttributeCount() {
        return currentElement.getAttributeNames().length;
    }

    public String getAttributeName(int index) {
        return unescapeXmlName(currentElement.getAttributeNames()[index]);
    }

    protected Object getParent() {
        return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.getChild(index);
    }

    protected int getChildCount() {
        return currentElement.getChildCount();
    }

    protected void reassignCurrentElement(Object current) {
        this.currentElement = (Xpp3Dom) current;
    }

}
