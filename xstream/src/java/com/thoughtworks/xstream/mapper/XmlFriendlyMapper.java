package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

/**
 * Mapper that ensures that all names in the serialization stream are XML friendly.
 * The mapper uses the {@link XmlMapperConfiguration} to allow configuration of the replacement chars in class and field names.
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 * @see XmlMapperConfiguration
 */
public class XmlFriendlyMapper extends MapperWrapper {

    private XmlMapperConfiguration configuration;
    
    public XmlFriendlyMapper(Mapper wrapped) {
        this(wrapped, new XmlMapperConfiguration());
    }

    public XmlFriendlyMapper(Mapper wrapped, XmlMapperConfiguration configuration) {
        super(wrapped);
        this.configuration = configuration;
    }
    
    /**
     * @deprecated As of 1.2, use {@link #XmlFriendlyMapper(Mapper)}
     */
    public XmlFriendlyMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public String serializedClass(Class type) {
        String name = super.serializedClass(type);

        // the $ used in inner class names is illegal as an xml element getNodeName
        name = name.replace('$', configuration.dollarReplacementInClass());

        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (name.charAt(0) == configuration.dollarReplacementInClass()) {
            name = configuration.noPackagePrefix() + name;
        }

        return name;
    }

    public Class realClass(String elementName) {
        // special case for classes named $Blah with no package; <-Blah> is illegal XML
        if (elementName.startsWith(configuration.noPackagePrefix()+configuration.dollarReplacementInClass())) {
            elementName = elementName.substring(configuration.noPackagePrefix().length());
        }

        // the $ used in inner class names is illegal as an xml element getNodeName
        elementName = elementName.replace(configuration.dollarReplacementInClass(), '$');

        return super.realClass(elementName);
    }

    public String serializedMember(Class type, String memberName) {
        return escape(super.serializedMember(type, memberName));
    }

    public String realMember(Class type, String serialized) {
        return unescape(super.realMember(type, serialized));
    }

    public String mapNameToXML(String javaName) {
        return escape(javaName);
    }

    public String mapNameFromXML(String xmlName) {
        return unescape(xmlName);
    }

    private String unescape(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for(int i = 0; i < length; i++) {
            char c = xmlName.charAt(i);
            if ( stringFoundAt(xmlName, i, configuration.underscoreReplacementInField())) {
                i += configuration.underscoreReplacementInField().length() - 1;
                result.append('_');
            } else if ( stringFoundAt(xmlName, i, configuration.dollarReplacementInField())) {
                i += configuration.dollarReplacementInField().length() - 1;
                result.append('$');
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

    private String escape(String javaName) {
        StringBuffer result = new StringBuffer();
        int length = javaName.length();
        for(int i = 0; i < length; i++) {
            char c = javaName.charAt(i);
            if (c == '$' ) {
                result.append(configuration.dollarReplacementInField());
            } else if (c == '_') {
                result.append(configuration.underscoreReplacementInField());
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
