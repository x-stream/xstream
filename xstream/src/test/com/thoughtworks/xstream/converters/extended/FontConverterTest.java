package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public class FontConverterTest extends TestCase {
    private XStream xstream;
    private Font in;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        in = new Font("Arial", Font.BOLD, 20);
    }

    public void testConvertsToFontThatEqualsOriginal() {
        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        // assert
        assertEquals(in, out);
    }

    public void testProducesFontThatHasTheSameAttributes() {
        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        // assert
        Map inAttributes = in.getAttributes();
        Map outAttributes = out.getAttributes();

        // these attributes don't have a valid .equals() method (bad Sun!), so we can't use them in the test.
        inAttributes.remove(TextAttribute.TRANSFORM);
        outAttributes.remove(TextAttribute.TRANSFORM);

        assertEquals(inAttributes, outAttributes);
    }

    public void testCorrectlyInitializesFontToPreventJvmCrash() {
        // If a font has not been constructed in the correct way, the JVM crashes horribly through some internal
        // native code, whenever the font is rendered to screen.

        // execute
        Font out = (Font) xstream.fromXML(xstream.toXML(in));

        Toolkit.getDefaultToolkit().getFontMetrics(out);
        // if the JVM hasn't crashed yet, we're good.

    }

}
