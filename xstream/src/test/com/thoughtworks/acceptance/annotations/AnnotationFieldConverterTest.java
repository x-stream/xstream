package com.thoughtworks.acceptance.annotations;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;


/**
 * Tests for using annotations to override field converters
 * 
 * @author Guilherme Silveira
 * @author Mauro Talevi
 */
public class AnnotationFieldConverterTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testDifferentConverterCanBeAnnotatedForFieldsOfSameType() {
        TaskWithAnnotations task = new TaskWithAnnotations("1981, 9, 18", "1981, 9, 18");
        String xml = ""
                + "<com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithAnnotations>\n"
                + "  <date>\n"
                + "    <cal>1981, 9, 18</cal>\n"
                + "  </date>\n"
                + "  <time>_1981, 9, 18_</time>\n"
                + "</com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithAnnotations>";
        assertBothWays(task, xml);
    }

    public static class TaskWithAnnotations {

        @XStreamConverter(FirstConverter.class)
        private String date;

        @XStreamConverter(SecundaryConverter.class)
        private String time;

        public TaskWithAnnotations(String date, String time) {
            this.date = date;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && TaskWithAnnotations.class.equals(obj.getClass())
                    && ((TaskWithAnnotations)obj).date.equals(date)
                    && ((TaskWithAnnotations)obj).time.equals(time);
        }
    }

    public void testNonAnnotatedConvertersCanBeDefinedFieldsOfSameType() {
        TaskWithoutAnnotations task = new TaskWithoutAnnotations("1981, 9, 18",
				"1981, 9, 18");
        xstream.registerConverter(new FirstConverter(), XStream.PRIORITY_VERY_HIGH);
        String xml = ""
                + "<com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithoutAnnotations>\n"
                + "  <date>\n"
                + "    <cal>1981, 9, 18</cal>\n"
                + "  </date>\n"
                + "  <time>\n"
                + "    <cal>1981, 9, 18</cal>\n"
                + "  </time>\n"
                + "</com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithoutAnnotations>";
        assertBothWays(task, xml);
    }

    public static class TaskWithoutAnnotations {

        private String date, time;

        public TaskWithoutAnnotations(String date, String time) {
            this.date = date;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && TaskWithoutAnnotations.class.equals(obj.getClass())
                    && ((TaskWithoutAnnotations)obj).date.equals(date)
                    && ((TaskWithoutAnnotations)obj).time.equals(time);
        }
    }

    public static class FirstConverter implements Converter {

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            String calendar = source.toString();
            writer.startNode("cal");
            writer.setValue(calendar);
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            reader.moveDown();
            String calendar = reader.getValue();
            reader.moveUp();
            return calendar;
        }

        public boolean canConvert(Class type) {
            return type.equals(String.class);
        }
    }

    public static class SecundaryConverter implements Converter {

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            writer.setValue("_"+source.toString() + "_");
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        	String value = reader.getValue();
            return value.substring(1,value.length()-1);
        }

        public boolean canConvert(Class type) {
            return type.equals(String.class);
        }
    }

}
