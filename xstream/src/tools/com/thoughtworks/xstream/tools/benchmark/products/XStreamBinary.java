package com.thoughtworks.xstream.tools.benchmark.products;

import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Uses XStream with binary format instead of XML.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 * @see XStream
 * @see BinaryStreamReader
 * @see BinaryStreamWriter
 */
public class XStreamBinary implements Product {

    private final XStream xstream;

    public XStreamBinary() {
        this.xstream = new XStream();
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.marshal(object, new BinaryStreamWriter(output));
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.unmarshal(new BinaryStreamReader(input));
    }

    public String toString() {
        return "XStream (binary format)";
    }

}
