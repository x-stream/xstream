package com.thoughtworks.xstream.io.xml;

import javax.xml.namespace.QName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a mapping of {@link QName} instances to Java class names
 * allowing class aliases and namespace aware mappings of QNames to class names.
 *
 * @author James Strachan
 * @version $Revision$
 */
public class QNameMap {

    // lets make the mapping a no-op unless we specify some mapping
    private Map qnameToJava;
    private Map javaToQName;
    private String defaultPrefix = "";
    private String defaultNamespace = "";

    /**
     * Returns the Java class name that should be used for the given QName.
     * If no explicit mapping has been made then the localPart of the QName is used
     * which is the normal default in XStream.
     */
    public String getJavaClassName(QName qname) {
        if (qnameToJava != null) {
            String answer = (String) qnameToJava.get(qname);
            if (answer != null) {
                return answer;
            }
        }
        return qname.getLocalPart();
    }

    /**
     * Returns the Java class name that should be used for the given QName.
     * If no explicit mapping has been made then the localPart of the QName is used
     * which is the normal default in XStream.
     */
    public QName getQName(String javaClassName) {
        if (javaToQName != null) {
            QName answer = (QName) javaToQName.get(javaClassName);
            if (answer != null) {
                return answer;
            }
        }
        return new QName(defaultNamespace, javaClassName, defaultPrefix);
    }

    /**
     * Registers the mapping of the Java class name to the QName
     */
    public synchronized void registerMapping(QName qname, String javaClassName) {
        if (javaToQName == null) {
            javaToQName = Collections.synchronizedMap(new HashMap());
        }
        if (qnameToJava == null) {
            qnameToJava = Collections.synchronizedMap(new HashMap());
        }
        javaToQName.put(javaClassName, qname);
        qnameToJava.put(qname, javaClassName);
    }

    /**
     * Registers the mapping of the type to the QName
     */
    public synchronized void registerMapping(QName qname, Class type) {
        registerMapping(qname, type.getName());
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }
}
