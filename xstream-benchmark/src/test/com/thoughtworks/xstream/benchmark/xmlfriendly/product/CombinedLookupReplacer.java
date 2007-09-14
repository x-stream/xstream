package com.thoughtworks.xstream.benchmark.xmlfriendly.product;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses a combined lookup and replaces characters.
 *
 * @author J&ouml;rg Schaible
 */
public class CombinedLookupReplacer implements Product {

    private final XStream xstream;
    private final int bufferIncrement;

    public CombinedLookupReplacer(int bufferIncrement) {
        this.bufferIncrement = bufferIncrement;
        this.xstream = new XStream(new XppDriver(new XmlFriendlyReplacer(bufferIncrement)));
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "Combined Lookup Replacer" + (bufferIncrement == 0 ? "" : (" (" + bufferIncrement + ")"));
    }
    
    public static class XmlFriendlyReplacer extends AbstractXmlFriendlyReplacer {

        public XmlFriendlyReplacer(int bufferIncrement) {
            super("_-", "__", bufferIncrement);
        }

        public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement, int bufferIncrement) {
            super(dollarReplacement, underscoreReplacement, bufferIncrement);
        }
        
        public String escapeName(String name) {
            return super.escapeByCombinedLookupReplacing(name);
        }
        
        public String unescapeName(String name) {
            return super.unescapeByCombinedLookupReplacing(name);
        }
    }
}
