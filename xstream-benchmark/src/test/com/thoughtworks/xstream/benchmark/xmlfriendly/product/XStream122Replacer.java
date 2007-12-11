/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.xmlfriendly.product;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses XmlFriendlyReplacer of XStream 1.2.2.
 *
 * @author J&ouml;rg Schaible
 */
public class XStream122Replacer implements Product {

    private final XStream xstream;

    public XStream122Replacer() {
        this.xstream = new XStream(new XppDriver(new XmlFriendlyReplacer()));
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "XStream 1.2.2 Replacer";
    }
    
    public static class XmlFriendlyReplacer extends com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer {

        private String dollarReplacement;
        private String underscoreReplacement;

        /**
         * Default constructor. 
         */
        public XmlFriendlyReplacer() {
            this("_-", "__", 0);
        }
        
        /**
         * Creates an XmlFriendlyReplacer with custom replacements
         * @param dollarReplacement the replacement for '$'
         * @param underscoreReplacement the replacement for '_'
         */
        public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement, int dummy) {
            this.dollarReplacement = dollarReplacement;
            this.underscoreReplacement = underscoreReplacement;
        }
        
        /**
         * Escapes name substituting '$' and '_' with replacement strings
         * @param name the name of attribute or node
         * @return The String with the escaped name
         */
        public String escapeName(String name) {
            StringBuffer result = new StringBuffer();
            int length = name.length();
            for(int i = 0; i < length; i++) {
                char c = name.charAt(i);
                if (c == '$' ) {
                    result.append(dollarReplacement);
                } else if (c == '_') {
                    result.append(underscoreReplacement);
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
        
        /**
         * Unescapes name re-enstating '$' and '_' when replacement strings are found
         * @param name the name of attribute or node
         * @return The String with unescaped name
         */
        public String unescapeName(String name) {
            StringBuffer result = new StringBuffer();
            int length = name.length();
            for(int i = 0; i < length; i++) {
                char c = name.charAt(i);
                if ( stringFoundAt(name, i, dollarReplacement)) {
                    i += dollarReplacement.length() - 1;
                    result.append('$');
                } else if ( stringFoundAt(name, i, underscoreReplacement)) {
                    i += underscoreReplacement.length() - 1;
                    result.append('_');
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
        
        private boolean stringFoundAt(String name, int i, String replacement) {
            if ( name.length() >= i + replacement.length() 
              && name.substring(i, i + replacement.length()).equals(replacement) ){
                return true;
            }
            return false;
        }

    }
}
