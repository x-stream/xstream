/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. September 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.Z;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

/**
 * Test XStream 1.1 compatibility.
 * 
 * @author J&ouml;rg Schaible
 */
public class XStream11CompatibilityTest extends AbstractAcceptanceTest {
    public static class ZConverter extends AbstractBasicConverter {

        public boolean canConvert(Class type) {
            return type.equals(Z.class);
        }

        protected Object fromString(String str) {
            return new Z("z");
        }

    }

    public void testUnmarshalsObjectFromXmlWithCustomDefaultConverterBasedOnAbstractBasicConverter() {

        xstream.registerConverter(new ZConverter(), -20);
        xstream.alias("z", Z.class);

        String xml =
                "<z>" +
                "  <any-old-suff/>" +
                "</z>";

        Z z = (Z) xstream.fromXML(xml);
        
        assertEquals("z", z.field);
    }

    public void testClassMapperCompatibility() {
        ClassMapper mapper = xstream.getClassMapper();
        assertEquals("string", mapper.serializedClass(String.class));
    }
}
