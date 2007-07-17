package com.thoughtworks.xstream;

/**
 * @author Guilherme Silveira
 * @since upcoming 
 */
public class ReadOnlyXStream {

    private final XStream xstream;

    public ReadOnlyXStream(XStream xstream) {
        this.xstream = xstream;
    }

    public Object fromXML(String xml) {
        return xstream.fromXML(xml);
    }

    public String toXML(Object obj) {
        return xstream.toXML(obj);
    }
}
