package com.thoughtworks.acceptance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

/**
 * @author Paul Hammant
 * @author Ian Cartwright
 * @author Mauro Talevi
 */
public class AttributeAliasTest extends AbstractAcceptanceTest {

    private TimeZoneChanger tzc;

    protected void setUp() throws Exception {
        super.setUp();
        tzc = new TimeZoneChanger();
        tzc.change("GMT");
    }

    protected void tearDown() throws Exception {
        tzc.reset();
        super.tearDown();
    }

    public void testWithAliasAndCustomConverter() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.aliasAttribute("id", ID.class);
        xstream.registerSingleValueConverter(new MyIDConverter());

        String expected =
                "<com.thoughtworks.acceptance.AttributeAliasTest-One id=\"hullo\">\n" +
                "  <two/>\n" +
                "</com.thoughtworks.acceptance.AttributeAliasTest-One>";
        assertBothWays(one, expected);
    }

    public void testWithoutAliasButWithCustomConverter() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.registerSingleValueConverter(new MyIDConverter());

        String expected =
                "<com.thoughtworks.acceptance.AttributeAliasTest-One>\n" +
                "  <id>hullo</id>\n" +
                "  <two/>\n" +
                "</com.thoughtworks.acceptance.AttributeAliasTest-One>";
        assertBothWays(one, expected);
    }

    public void testWithAliasAndKnownConverter() throws Exception {
        Three three = new Three();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        xstream.aliasAttribute("date", Date.class);
        String expected =
            "<com.thoughtworks.acceptance.AttributeAliasTest-Three date=\"2006-02-19 00:00:00.0 GMT\"/>";
        assertBothWays(three, expected);
    }

    public void testWithAliasButWithKnownConverter() throws Exception {
        Three three = new Three();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        String expected =
            "<com.thoughtworks.acceptance.AttributeAliasTest-Three>\n" +
            "  <date>2006-02-19 00:00:00.0 GMT</date>\n" +
            "</com.thoughtworks.acceptance.AttributeAliasTest-Three>";
        assertBothWays(three, expected);
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



