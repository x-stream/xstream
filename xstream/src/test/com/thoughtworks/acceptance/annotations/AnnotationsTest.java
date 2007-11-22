package com.thoughtworks.acceptance.annotations;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;


/**
 * Tests for annotation detection.
 * 
 * @author Chung-Onn Cheong
 * @author Mauro Talevi
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class AnnotationsTest extends AbstractAcceptanceTest {

    @XStreamAlias("param")
    public static class ParameterizedContainer {

        private ParameterizedType<InternalType> type;

        public ParameterizedContainer() {
            type = new ParameterizedType<InternalType>(new InternalType());
        }

    }

    @XStreamAlias("param")
    public static class DoubleParameterizedContainer {

        private ArrayList<ArrayList<InternalType>> list;

        public DoubleParameterizedContainer() {
            list = new ArrayList<ArrayList<InternalType>>();
            list.add(new ArrayList<InternalType>());
            list.get(0).add(new InternalType());
        }

    }

    @XStreamAlias("second")
    public static class InternalType {
        @XStreamAlias("aliased")
        private String original = "value";
    }

    public static class ParameterizedType<T> {
        @XStreamAlias("fieldAlias")
        private T object;

        public ParameterizedType(T object) {
            this.object = object;
        }
    }

    public void testAreDetectedInParameterizedTypes() {
        xstream.processAnnotations(ParameterizedContainer.class);
        String xml = ""
            + "<param>\n"
            + "  <type>\n"
            + "    <fieldAlias class=\"second\">\n"
            + "      <aliased>value</aliased>\n"
            + "    </fieldAlias>\n"
            + "  </type>\n"
            + "</param>";
        assertBothWays(new ParameterizedContainer(), xml);
    }

    public void testAreDetectedInNestedParameterizedTypes() {
        xstream.processAnnotations(DoubleParameterizedContainer.class);
        String xml = ""
            + "<param>\n"
            + "  <list>\n"
            + "    <list>\n"
            + "      <second>\n"
            + "        <aliased>value</aliased>\n"
            + "      </second>\n"
            + "    </list>\n"
            + "  </list>\n"
            + "</param>";
        assertBothWays(new DoubleParameterizedContainer(), xml);
    }
}
