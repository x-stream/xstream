package com.thoughtworks.acceptance.annotations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
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
        TaskWithAnnotations task = new TaskWithAnnotations(new GregorianCalendar(1981, 9, 18),
				new GregorianCalendar(0, 0, 0, 30, 20));
		String xml = "<com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithAnnotations>\n"
				+ "  <date>\n"
				+ "    <cal>372225600000</cal>\n"
				+ "  </date>\n"
                + "  <time>-62167351200000</time>\n"
				+ "</com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithAnnotations>";
		assertBothWays(task, xml);
	}

    public static class TaskWithAnnotations {

        @XStreamConverter(TreeCalendarConverter.class)
        private Calendar date;

        @XStreamConverter(SingleValueCalendarConverter.class)
        private Calendar time;

        public TaskWithAnnotations(Calendar date, Calendar time) {
            this.date = date;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && TaskWithAnnotations.class.equals(obj.getClass())
                    && ((TaskWithAnnotations) obj).date.equals(date)
                    && ((TaskWithAnnotations) obj).time.equals(time);
        }
    }

    public void testNonAnnotatedConvertersCanBeDefinedFieldsOfSameType() {        
        TaskWithoutAnnotations task = new TaskWithoutAnnotations(new GregorianCalendar(1981, 9, 18),
                new GregorianCalendar(0, 0, 0, 30, 20));
        xstream.registerConverter(new TreeCalendarConverter());
        String xml = "<com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithoutAnnotations>\n"
                + "  <date>\n"
                + "    <cal>372225600000</cal>\n"
                + "  </date>\n"
                + "  <time>\n"
                + "    <cal>-62167351200000</cal>\n"
                + "  </time>\n"
                + "</com.thoughtworks.acceptance.annotations.AnnotationFieldConverterTest_-TaskWithoutAnnotations>";
        assertBothWays(task, xml);
    }

    public static class TaskWithoutAnnotations {

        private Calendar date;

        private Calendar time;

        public TaskWithoutAnnotations(Calendar date, Calendar time) {
            this.date = date;
            this.time = time;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && TaskWithoutAnnotations.class.equals(obj.getClass())
                    && ((TaskWithoutAnnotations) obj).date.equals(date)
                    && ((TaskWithoutAnnotations) obj).time.equals(time);
        }
    }
   
    public static class TreeCalendarConverter implements Converter {

		public void marshal(Object source, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			Calendar calendar = (Calendar) source;
			writer.startNode("cal");
			writer.setValue(String.valueOf(calendar.getTime().getTime()));
			writer.endNode();
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			reader.moveDown();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date(Long.parseLong(reader.getValue())));
			reader.moveUp();
			return calendar;
		}

		public boolean canConvert(Class type) {
			return type.equals(GregorianCalendar.class);
		}
	}

    public static class SingleValueCalendarConverter implements Converter {

        public void marshal(Object source, HierarchicalStreamWriter writer,
                MarshallingContext context) {
            Calendar calendar = (Calendar) source;
            writer.setValue(String.valueOf(calendar.getTime().getTime()));
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date(Long.parseLong(reader.getValue())));
            return calendar;
        }

        public boolean canConvert(Class type) {
            return type.equals(GregorianCalendar.class);
        }
    }

}
