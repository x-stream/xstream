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

/**
 * Allows replacement of Strings in XML-friendly drivers. The default replacements are:
 * <ul>
 * <li><b>$</b> (dollar) chars are replaced with <b>_-</b> (underscore dash) string.<br>
 * </li>
 * <li><b>_</b> (underscore) chars are replaced with <b>__</b> (double underscore) string.<br>
 * </li>
 * </ul>
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @author Tatu Saloranta
 * @since 1.2
 * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
 */
@Deprecated
public class XmlFriendlyReplacer extends XmlFriendlyNameCoder {

    /**
     * Default constructor.
     * 
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    @Deprecated
    public XmlFriendlyReplacer() {
        this("_-", "__");
    }

    /**
     * Creates an XmlFriendlyReplacer with custom replacements
     * 
     * @param dollarReplacement the replacement for '$'
     * @param underscoreReplacement the replacement for '_'
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    @Deprecated
    public XmlFriendlyReplacer(final String dollarReplacement, final String underscoreReplacement) {
        super(dollarReplacement, underscoreReplacement);
    }

    /**
     * Escapes name substituting '$' and '_' with replacement strings
     * 
     * @param name the name of attribute or node
     * @return The String with the escaped name
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    @Deprecated
    public String escapeName(final String name) {
        return super.encodeNode(name);
    }

    /**
     * Unescapes name re-enstating '$' and '_' when replacement strings are found
     * 
     * @param name the name of attribute or node
     * @return The String with unescaped name
     * @deprecated As of 1.4, use {@link XmlFriendlyNameCoder} instead
     */
    @Deprecated
    public String unescapeName(final String name) {
        return super.decodeNode(name);
    }

}
