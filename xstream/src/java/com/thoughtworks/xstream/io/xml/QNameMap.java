/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.io.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;


/**
 * Represents a mapping of {@link QName} instances to Java class names allowing class aliases and namespace aware
 * mappings of QNames to class names.
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class QNameMap {

    // lets make the mapping a no-op unless we specify some mapping
    private Map<QName, String> qnameToJava;
    private Map<String, QName> javaToQName;
    private String defaultPrefix = "";
    private String defaultNamespace = "";

    /**
     * Returns the Java class name that should be used for the given QName. If no explicit mapping has been made then
     * the localPart of the QName is used which is the normal default in XStream.
     */
    public String getJavaClassName(final QName qname) {
        if (qnameToJava != null) {
            final String answer = qnameToJava.get(qname);
            if (answer != null) {
                return answer;
            }
        }
        return qname.getLocalPart();
    }

    /**
     * Returns the Java class name that should be used for the given QName. If no explicit mapping has been made then
     * the localPart of the QName is used which is the normal default in XStream.
     */
    public QName getQName(final String javaClassName) {
        if (javaToQName != null) {
            final QName answer = javaToQName.get(javaClassName);
            if (answer != null) {
                return answer;
            }
        }
        return new QName(defaultNamespace, javaClassName, defaultPrefix);
    }

    /**
     * Registers the mapping of the Java class name to the QName
     */
    public synchronized void registerMapping(final QName qname, final String javaClassName) {
        if (javaToQName == null) {
            javaToQName = Collections.synchronizedMap(new HashMap<String, QName>());
        }
        if (qnameToJava == null) {
            qnameToJava = Collections.synchronizedMap(new HashMap<QName, String>());
        }
        javaToQName.put(javaClassName, qname);
        qnameToJava.put(qname, javaClassName);
    }

    /**
     * Registers the mapping of the type to the QName
     */
    public synchronized void registerMapping(final QName qname, final Class<?> type) {
        registerMapping(qname, type.getName());
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(final String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public void setDefaultPrefix(final String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }
}
