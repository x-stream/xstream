package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

/**
 * Mapper that ensures that all names in the serialization stream are XML friendly.
 *
 * <b>$</b> (dollar) chars appearing in class names are replaced with <b>_</b> (underscore) chars.<br>
 * <b>$</b> (dollar) chars appearing in field names are replaced with <b>_DOLLAR_</b> string.<br>
 * <b>_</b> (underscore) chars appearing in field names are replaced with <b>__</b> (double underscore) string.<br>
 *
 * @author Joe Walnes
 */
public class XmlFriendlyMapper extends MapperWrapper {

    public XmlFriendlyMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public String lookupName(Class type) {
        String name = super.lookupName(type);

        // the $ used in inner class names is illegal as an xml element getNodeName
        name = name.replace('$', '-');

        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (name.charAt(0) == '-') {
            name = "default" + name;
        }

        return name;
    }

    public Class lookupType(String elementName) {

        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (elementName.startsWith("default-")) {
            elementName = elementName.substring(7);
        }

        // the $ used in inner class names is illegal as an xml element getNodeName
        elementName = elementName.replace('-', '$');

        return super.lookupType(elementName);
    }

    public String mapNameToXML(String javaName) {
        StringBuffer result = new StringBuffer();
        int length = javaName.length();
        for(int i = 0; i < length; i++) {
            char c = javaName.charAt(i);
            if (c == '$') {
                result.append("_DOLLAR_");
            } else if (c == '_') {
                result.append("__");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String mapNameFromXML(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for(int i = 0; i < length; i++) {
            char c = xmlName.charAt(i);
            if (c == '_') {
                if (xmlName.charAt(i + 1)  == '_') {
                    i++;
                    result.append('_');
                } else if (xmlName.length() >= i + 8 && xmlName.substring(i + 1, i + 8).equals("DOLLAR_")) {
                    i += 7;
                    result.append('$');
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
}
