package com.thoughtworks.acceptance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * @author Paul Hammant
 * @author Ian Cartwright
 * @author Mauro Talevi
 */
public class AttributeAliasTest extends AbstractAcceptanceTest {

    public void testSerializationOfAttributePossibleWithAliasAndKnownConverter() throws Exception {
        Three three = new Three();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        xstream.attributeAlias("date", Date.class);
        assertEquals(
                "<com.thoughtworks.acceptance.AttributeAliasTest-Three date=\"2006-02-19 00:00:00.0 GMT\"/>",
                xstream.toXML(three));
    }

    public void testDeSerializationOfAttributePossibleWithAliasAndKnownConverter() throws Exception {
        xstream.attributeAlias("date", Date.class);
        Three three = (Three) xstream.fromXML(
                "<com.thoughtworks.acceptance.AttributeAliasTest-Three date=\"2006-02-19 00:00:00.0 GMT\"/>");

        assertEquals(three.date.toString(), "Sun Feb 19 00:00:00 GMT 2006");
    }

    public void testSerializationOfAttributePossibleWithAliasAndCustomConverter() {
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

    public void testDeSerializationOfAttributePossibleWithAliasAndCustomConverter() {
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

    public static class Three {
        public Date date;
    }

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

        public String toString(Object obj) {
            return ((ID) obj).value;
        }

        public Object fromString(String str) {
            return new ID(str);
        }
    }

}



