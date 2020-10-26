/*
 * Copyright (C) 2006, 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.awt.font.TextAttribute;

import com.thoughtworks.xstream.converters.reflection.AbstractAttributedCharacterIteratorAttributeConverter;


/**
 * A converter for {@link TextAttribute} constants to a string.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class TextAttributeConverter extends AbstractAttributedCharacterIteratorAttributeConverter<TextAttribute> {

    /**
     * Constructs a TextAttributeConverter.
     * 
     * @since 1.2.2
     */
    public TextAttributeConverter() {
        super(TextAttribute.class);
    }
}
