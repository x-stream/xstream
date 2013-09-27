/*
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.Converter;

/**
 * A special converter that prevents self-serialization. The serializing XStream instance
 * adds a converter of this type to prevent self-serialization and will throw an
 * exception instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker}
 */
public class SelfStreamingInstanceChecker extends com.thoughtworks.xstream.core.util.SelfStreamingInstanceChecker {

    public SelfStreamingInstanceChecker(Converter defaultConverter, Object xstream) {
        super(defaultConverter, xstream);
    }

}
