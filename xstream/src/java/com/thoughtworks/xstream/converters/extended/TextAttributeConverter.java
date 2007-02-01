package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.reflection.AbstractAttributedCharacterIteratorAttributeConverter;

import java.awt.font.TextAttribute;


/**
 * A converter for {@link TextAttribute} constants.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class TextAttributeConverter extends
    AbstractAttributedCharacterIteratorAttributeConverter {

    /**
     * Constructs a TextAttributeConverter.
     * 
     * @since upcoming
     */
    public TextAttributeConverter() {
        super(TextAttribute.class);
    }
}
