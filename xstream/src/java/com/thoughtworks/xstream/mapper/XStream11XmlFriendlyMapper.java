package com.thoughtworks.xstream.mapper;


/**
 * Mapper that ensures that all names in the serialization stream are read in an XML friendly way.
 * <ul>
 * <li><b>_</b> (underscore) chars appearing in class names are replaced with <b>$<br> (dollar)</li>
 * <li><b>_DOLLAR_</b> string appearing in field names are replaced with <b>$<br> (dollar)</li>
 * <li><b>__</b> string appearing in field names are replaced with <b>_<br> (underscore)</li>
 * <li><b>default</b> is the prefix for class names with no package.</li>
 * </ul>
 * 
 * @author Joe Walnes
 * @author Mauro Talevi
 */
public class XStream11XmlFriendlyMapper extends AbstractXmlFriendlyMapper {

    public XStream11XmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }

    public Class realClass(String elementName) {
        return super.realClass(unescapeClassName(elementName));
    }

    public String realMember(Class type, String serialized) {
        return unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameFromXML(String xmlName) {
        return unescapeFieldName(xmlName);
    }

    
}
