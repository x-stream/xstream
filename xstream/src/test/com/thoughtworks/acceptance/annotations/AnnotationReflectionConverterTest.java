package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class AnnotationReflectionConverterTest extends AbstractAcceptanceTest {

    public static class CustomConverter implements Converter {

        private static int total = 0;

        public CustomConverter() {
            total++ ;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }

        public boolean canConvert(Class type) {
            return type.equals(Double.class);
        }

    }

    public static class Account {
        @XStreamConverter(CustomConverter.class)
        private Double value;

        public Account() {
            this.value = Math.random();
        }
    }

    public static class Client {
        @XStreamConverter(CustomConverter.class)
        private Double value;

        public Client() {
            this.value = Math.random();
        }
    }

    public void testCachesConverterPerField() {
        int before = CustomConverter.total;
        toXML(new Account());
        int after = CustomConverter.total;
        assertEquals(before + 1, after);
    }

    public void testCachesConverterPerFieldInDifferentContexts() {
        int before = CustomConverter.total;
        toXML(new Account());
        toXML(new Client());
        int after = CustomConverter.total;
        assertEquals(before + 1, after);
    }
}
