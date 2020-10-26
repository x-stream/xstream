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

package com.thoughtworks.xstream.converters.extended;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A Converter for the XML Schema datatype <a href="http://www.w3.org/TR/xmlschema-2/#duration">duration</a> and the
 * Java type {@link Duration}.
 * <p>
 * The implementation uses a {@link DatatypeFactory} to create Duration objects. If no factory is provided and the
 * instantiation of the internal factory fails with a {@link DatatypeConfigurationException} , the converter will not
 * claim the responsibility for Duration objects.
 * </p>
 * 
 * @author John Kristian
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class DurationConverter extends AbstractSingleValueConverter {
    private final DatatypeFactory factory;

    public DurationConverter() {
        this(new Object() {
            DatatypeFactory getFactory() {
                try {
                    return DatatypeFactory.newInstance();
                } catch (final DatatypeConfigurationException e) {
                    return null;
                }
            }
        }.getFactory());
    }

    public DurationConverter(final DatatypeFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return factory != null && type != null && Duration.class.isAssignableFrom(type);
    }

    @Override
    public Object fromString(final String str) {
        return factory.newDuration(str);
    }
}
