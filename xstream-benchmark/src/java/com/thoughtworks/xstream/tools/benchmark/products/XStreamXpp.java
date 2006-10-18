package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses XStream with the XPP driver for parsing XML.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream
 * @see XppDriver
 */
public class XStreamXpp implements Product {

    private final XStream xstream;

    public XStreamXpp() {
        this.xstream = new XStream(new XppDriver());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream (XML with XPP parser)";
    }

}
