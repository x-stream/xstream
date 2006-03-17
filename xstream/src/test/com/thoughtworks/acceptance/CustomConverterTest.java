package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import java.text.DecimalFormat;
import java.text.ParseException;

public class CustomConverterTest extends AbstractAcceptanceTest {
    
    private final class DoubleConverter implements SingleValueConverter {
        private final DecimalFormat formatter;

        private DoubleConverter() {
            formatter = new DecimalFormat("#,###,##0");
        }

        public boolean canConvert(Class type) {
            return type == double.class || type == Double.class;
        }

        public String toString(Object obj) {
            return this.formatter.format(obj);
        }

        public Object fromString(String str) {
            try {
                // the formatter will chose the most appropriate format ... Long
                return this.formatter.parseObject(str);
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class DoubleWrapper {
        Double d;

        public DoubleWrapper(double d) {
            this.d = new Double(d);
        }
        
        protected DoubleWrapper() {
            // JDK 1.3 issue
        }
    }

    public void testWrongObjectTypeReturned() {
        xstream.alias("dw", DoubleWrapper.class);
        xstream.registerConverter(new DoubleConverter());

        String xml = "" +
            "<dw>\n" +
            "  <d>-92.000.000</d>\n" +
            "</dw>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().indexOf(Long.class.getName()) > 0);
        }
    }
}
