package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Uses XStream with the DOM driver for parsing XML.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream
 * @see DomDriver
 */
public class XStreamDom implements Product {

    private final XStream xstream;

    public XStreamDom() {
        this.xstream = new XStream(new DomDriver());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream (XML with DOM parser)";
    }

}
