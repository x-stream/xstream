package com.thoughtworks.xstream.io.xml;

/**
 * Allows replacement of Strings in xml-friendly wrappers.
 * 
 * The default constructor uses:
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_DOLLAR_</b> string.<br></li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br></li>
 * <li><b>default</b> as the prefix for class names with no package.</li>
 * </ul>
 * 
 * @author Mauro Talevi
 */
public class XmlFriendlyReplacer {

    private String dollarReplacement;
    private String underscoreReplacement;
    private String noPackagePrefix; // this may not be needed unless we use '-' as dollar replacement

    /**
     * Default constructor. 
     */
    public XmlFriendlyReplacer() {
        this("_DOLLAR_", "__", "default");
    }
    
    /**
     * Creates an XmlMapperConfiguration with custom replacement chars and strings.
     * @param dollarReplacement
     * @param underscoreReplacement
     * @param noPackagePrefix
     */
    public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement, String noPackagePrefix) {
        this.dollarReplacement = dollarReplacement;
        this.underscoreReplacement = underscoreReplacement;
        this.noPackagePrefix = noPackagePrefix;
    }
    
    public String escapeName(String javaName) {
        StringBuffer result = new StringBuffer();
        int length = javaName.length();
        for(int i = 0; i < length; i++) {
            char c = javaName.charAt(i);
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
    
    public String unescapeName(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for(int i = 0; i < length; i++) {
            char c = xmlName.charAt(i);
            if ( stringFoundAt(xmlName, i, underscoreReplacement)) {
                i += underscoreReplacement.length() - 1;
                result.append('_');
            } else if ( stringFoundAt(xmlName, i, dollarReplacement)) {
                i += dollarReplacement.length() - 1;
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

}
