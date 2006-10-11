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
 * @author J&ouml;rg Schaible
 */
public class AttributeTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneChanger.change("GMT");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testAllowsAttributeWithCustomConverterAndFieldName() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("id", ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithCustomConverterAndDifferentFieldName() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("foo", ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one>\n" +
                "  <id>hullo</id>\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    public void testAllowsAttributeWithKnownConverterAndFieldName() throws Exception {
        Three three = new Three();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        xstream.alias("three", Three.class);
        xstream.useAttributeFor("date", Date.class);
        
        String expected =
            "<three date=\"2006-02-19 00:00:00.0 GMT\"/>";
        assertBothWays(three, expected);
    }

    public void testAllowsAttributeWithArbitraryFieldType() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithNullAttribute() {
        One one = new One();
        one.two = new Two();

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one>\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }    
    
    public void testAllowsAttributeToBeAliased() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.aliasAttribute("id-alias", "id");
        xstream.useAttributeFor("id", ID.class);        
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id-alias=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }
    
    public void testCanHandleNullValues() {
        C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        String expected =
            "<C>\n" +
            "  <i>0</i>\n" +
            "</C>";
        assertBothWays(c, expected);
    }
    
    public void testCanHandlePrimitiveValues() {
        C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(int.class);
        String expected ="<C i=\"0\"/>";
        assertBothWays(c, expected);
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
            return obj == null ? null : ((ID) obj).value;
        }

        public Object fromString(String str) {
            return new ID(str);
        }
    }
    
    static class C
    {
        private Date dt;
        private String str;
        private int i;

        C(Date dt, String st, int i)
        {
            super();
            this.dt = dt;
            this.str = st;
            this.i = i;
        }
    }
}



