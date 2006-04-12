package com.thoughtworks.xstream.mapper;

/**
 * Allows configuration of replacement chars and Strings used by XmlFriendlyMapper.  
 * 
 * The default constructor uses:
 * <ul>
 * <li><b>$</b> (dollar) chars appearing in class names are replaced with <b>_</b> (underscore) chars.<br></li>
 * <li><b>$</b> (dollar) chars appearing in field names are replaced with <b>_DOLLAR_</b> string.<br></li>
 * <li><b>_</b> (underscore) chars appearing in field names are replaced with <b>__</b> (double underscore) string.<br></li>
 * <li><b>default</b> as the prefix for class names with no package.</li>
 * </ul>
 * 
 * @author Mauro Talevi
 */
public class XmlMapperConfiguration {

    private char dollarReplacementInClass;
    private String dollarReplacementInField;
    private String underscoreReplacementInField;
    private String noPackagePrefix;


    /**
     * Default constructor. 
     */
    public XmlMapperConfiguration() {
        this('-', "_DOLLAR_", "__", "default");
    }
    
    /**
     * Creates an XmlMapperConfiguration with custom replacement chars and strings.
     * @param dollarReplacementInClass
     * @param dollarReplacementInField
     * @param underscoreReplacementInField
     * @param noPackagePrefix
     */
    public XmlMapperConfiguration(char dollarReplacementInClass, String dollarReplacementInField, String underscoreReplacementInField, String noPackagePrefix) {
        this.dollarReplacementInClass = dollarReplacementInClass;
        this.dollarReplacementInField = dollarReplacementInField;
        this.underscoreReplacementInField = underscoreReplacementInField;
        this.noPackagePrefix = noPackagePrefix;
    }

    public char dollarReplacementInClass() {
        return dollarReplacementInClass;
    }

    public String dollarReplacementInField() {
        return dollarReplacementInField;
    }

    public String underscoreReplacementInField() {
        return underscoreReplacementInField;
    }

    public String noPackagePrefix() {
        return noPackagePrefix;
    }

}
