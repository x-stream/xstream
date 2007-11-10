package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Tests for using annotations to override field converters
 * 
 * @author Guilherme Silveira
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class AnnotationFieldConverterTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("annotatedTask", TaskWithAnnotations.class);
        xstream.alias("derivedTask", DerivedTask.class);
        xstream.alias("taskContainer", TaskContainer.class);
    }

    public void testDifferentConverterCanBeAnnotatedForFieldsOfSameType() {
        final TaskWithAnnotations task = new TaskWithAnnotations("Tom", "Dick", "Harry");
        final String xml = ""
                + "<annotatedTask>\n"
                + "  <name1 str=\"Tom\"/>\n"
                + "  <name2>_Dick_</name2>\n"
                + "  <name3>Harry</name3>\n"
                + "</annotatedTask>";
        assertBothWays(task, xml);
    }

    public void testConverterCanBeAnnotatedForHiddenFields() {
        final DerivedTask task = new DerivedTask("Tom", "Dick", "Harry");
        final String xml = ""
                + "<derivedTask>\n"
                + "  <name1 defined-in=\"annotatedTask\" str=\"Tom\"/>\n"
                + "  <name2>_Dick_</name2>\n"
                + "  <name3 defined-in=\"annotatedTask\">Harry</name3>\n"
                + "  <name1>Harry</name1>\n"
                + "  <name3 str=\"Tom\"/>\n"
                + "</derivedTask>";
        assertBothWays(task, xml);
    }

    public void testAnnotationsAreFoundInReferencesTypes() {
        final TaskContainer taskContainer = new TaskContainer();
        final String xml = ""
                + "<taskContainer>\n"
                + "  <task>\n"
                + "    <name1 defined-in=\"annotatedTask\" str=\"Tom\"/>\n"
                + "    <name2>_Dick_</name2>\n"
                + "    <name3 defined-in=\"annotatedTask\">Harry</name3>\n"
                + "    <name1>Harry</name1>\n"
                + "    <name3 str=\"Tom\"/>\n"
                + "  </task>\n"
                + "</taskContainer>";
        assertEquals(taskContainer, xstream.fromXML(xml));
    }

    public static class TaskWithAnnotations {

        @XStreamConverter(FirstConverter.class)
        private final String name1;

        @XStreamConverter(SecondaryConverter.class)
        private final String name2;
        private final String name3;

        public TaskWithAnnotations(final String name1, final String name2, final String name3) {
            this.name1 = name1;
            this.name2 = name2;
            this.name3 = name3;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                    && TaskWithAnnotations.class.isAssignableFrom(obj.getClass())
                    && ((TaskWithAnnotations)obj).name1.equals(name1)
                    && ((TaskWithAnnotations)obj).name2.equals(name2)
                    && ((TaskWithAnnotations)obj).name3.equals(name3);
        }
    }
    
    public static class DerivedTask extends TaskWithAnnotations {
        private final String name1;

        @XStreamConverter(FirstConverter.class)
        private final String name3;

        public DerivedTask(final String name1, final String name2, final String name3) {
            super(name1, name2, name3);
            this.name1 = name3;
            this.name3 = name1;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                    && DerivedTask.class.isAssignableFrom(obj.getClass())
                    && ((DerivedTask)obj).name1.equals(name1)
                    && ((DerivedTask)obj).name3.equals(name3)
                    && super.equals(obj);
        }
    }
    
    public static class TaskContainer {
        private final DerivedTask task = new DerivedTask("Tom", "Dick", "Harry");

        @Override
        public boolean equals(final Object obj) {
            return obj != null
                    && TaskContainer.class.equals(obj.getClass())
                    && task.equals(((TaskContainer)obj).task);
        }
    }

    public static class FirstConverter implements Converter {

        public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
            final String str = source.toString();
            writer.addAttribute("str", str);
        }

        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final String str = reader.getAttribute("str");
            return str;
        }

        public boolean canConvert(final Class type) {
            return type.equals(String.class);
        }
    }

    public static class SecondaryConverter implements Converter {

        public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
            writer.setValue("_"+source.toString() + "_");
        }

        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        	final String value = reader.getValue();
            return value.substring(1,value.length()-1);
        }

        public boolean canConvert(final Class type) {
            return type.equals(String.class);
        }
    }

}
