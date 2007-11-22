package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * Tests annotation to omit a field.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class OmitFieldTest extends AbstractAcceptanceTest {
    @XStreamAlias("apartment")
    public static class Apartment {

        @XStreamOmitField
        int size;

        protected Apartment(int size) {
            this.size = size;
        }
    }

    public void testAnnotation() {
        xstream.processAnnotations(Apartment.class);
        Apartment ap = new Apartment(5);
        String expectedXml = "<apartment/>";
        assertBothWays(ap, expectedXml);
    }
}
