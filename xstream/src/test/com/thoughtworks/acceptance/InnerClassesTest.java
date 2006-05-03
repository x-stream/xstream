package com.thoughtworks.acceptance;

public class InnerClassesTest extends AbstractAcceptanceTest {

    public void testSerializedInnerClassMaintainsReferenceToOuterClass() {

        Outer outer = new Outer("THE-OUTER-NAME", "THE-INNER-NAME");
        Outer.Inner inner = outer.getInner();

        assertEquals("Hello from THE-INNER-NAME (inside THE-OUTER-NAME)", inner.getMessage());

        String xml = xstream.toXML(inner);

        String expectedXml = ""
                + "<com.thoughtworks.acceptance.Outer_-Inner>\n"
                + "  <innerName>THE-INNER-NAME</innerName>\n"
                + "  <outer-class>\n"
                + "    <inner reference=\"../..\"/>\n"
                + "    <outerName>THE-OUTER-NAME</outerName>\n"
                + "  </outer-class>\n"
                + "</com.thoughtworks.acceptance.Outer_-Inner>";
        assertEquals(expectedXml, xml);

        Outer.Inner newInner = (Outer.Inner) xstream.fromXML(xml);

        assertEquals("Hello from THE-INNER-NAME (inside THE-OUTER-NAME)", newInner.getMessage());
    }
}

class Outer {

    private Inner inner;
    private String outerName;

    public Outer(String outerName, String innerName) {
        inner = new Inner(innerName);
        this.outerName = outerName;
    }

    public Inner getInner() {
        return inner;
    }

    public class Inner {
        private String innerName;

        public Inner(String innerName) {
            this.innerName = innerName;
        }

        public String getMessage() {
            return "Hello from " + innerName + " (inside " + outerName + ")";
        }
    }
}


