package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * @author Paul Hammant 
 * @author Ian Cartwright
 * @author Mauro Talevi
 */
public class AttributeAliasTest extends AbstractAcceptanceTest {

    public void testSerializationOfAttributePossibleWithAliasAndConverter() {

        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.attributeAlias("id", ID.class);
        xstream.registerSingleValueConverter(new MyIDConverter());
        
        assertEquals(
                "<com.thoughtworks.acceptance.AttributeAliasTest-One id=\"hullo\">\n" +
                "  <two/>\n" +
                "</com.thoughtworks.acceptance.AttributeAliasTest-One>",
                xstream.toXML(one));

    }

    public void testDeSerializationOfAttributePossibleWithAliasAndConverter() {

        xstream.attributeAlias("id", ID.class);
        xstream.registerSingleValueConverter(new MyIDConverter());
 
        One one = (One) xstream.fromXML(
                "<com.thoughtworks.acceptance.AttributeAliasTest-One id=\"hullo\">\n" +
                "  <two/>\n" +
                "</com.thoughtworks.acceptance.AttributeAliasTest-One>");

        assertEquals(one.id.value, "hullo");
        assertNotNull(one.two);

    }



    public static class One implements HasID {
        public ID id;
        public Two two;

        public void setID(ID id) {
            this.id = id;
        }
    }

    public static interface HasID {
        void setID(ID id);
    }
    
    public static class Two {}

    public static class ID {
        public ID(String value) {
            this.value = value;
        }

        public String value;
    }

    private static class MyIDConverter extends AbstractSingleValueConverter {
        public boolean canConvert(Class type) {
            return type.equals(ID.class);
        }

        @Override
        public String toString(Object obj) {
            return ((ID) obj).value;
        }

        @Override
        public Object fromString(String str) {            
            return new ID(str);
        }
    }

}



