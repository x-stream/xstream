package com.thoughtworks.xstream.io.xml;

/**
 * Allows replacement of Strings in xml-friendly drivers.
 * 
 * The default replacements are:
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.<br></li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br></li>
 * </ul>
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public class XmlFriendlyReplacer {

    private String dollarReplacement;
    private String underscoreReplacement;

    /**
     * Default constructor. 
     */
    public XmlFriendlyReplacer() {
        this("_-", "__");
    }
    
    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     */
    public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement) {
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
            if ( stringFoundAt(name, i, underscoreReplacement)) {
                i += underscoreReplacement.length() - 1;
                result.append('_');
            } else if ( stringFoundAt(name, i, dollarReplacement)) {
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
