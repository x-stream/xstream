package com.thoughtworks.xstream.alias;

public class XmlFriendlyClassMapper extends ClassMapperWrapper {

    public XmlFriendlyClassMapper(ClassMapper wrapped) {
        super(wrapped);
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
