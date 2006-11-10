package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DriverEndToEndTestSuite extends TestSuite {

    public static Test suite() {
        return new DriverEndToEndTestSuite();
    }

    public DriverEndToEndTestSuite() {
        super(DriverEndToEndTestSuite.class.getName());
        addDriverTest(new Dom4JDriver());
        addDriverTest(new DomDriver());
        addDriverTest(new JDomDriver());
        addDriverTest(new StaxDriver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        addDriverTest(new XomDriver());
    }

    private void test(HierarchicalStreamDriver driver) {
        XStream xstream = new XStream(driver);

        Object in = new Software("some vendor", "some name");
        String xml = xstream.toXML(in);
        Object out = xstream.fromXML(xml);

        Assert.assertEquals(in, out);
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        String testName = getShortName(driver);
        addTest(new TestCase(testName) {
            protected void runTest() throws Throwable {
                test(driver);
            }
        });
    }

    private String getShortName(HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }

}
