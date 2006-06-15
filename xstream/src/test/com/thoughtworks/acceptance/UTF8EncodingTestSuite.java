package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Sanjiv Jivan
 * @author Guilherme Silveira
 */
public class UTF8EncodingTestSuite extends TestSuite {

	public static class UTF8TestObject extends StandardObject {
		private String data;
	}

	public UTF8EncodingTestSuite() {
		super(UTF8EncodingTestSuite.class.getName());
		// addDriverTest(new Dom4JDriver());
		addDriverTest(new DomDriver("UTF-8"));
		// addDriverTest(new JDomDriver());
		// addDriverTest(new StaxDriver());
		// addDriverTest(new XppDomDriver());
		// addDriverTest(new XppDriver());
		// addDriverTest(new XomDriver());
	}

	private void test(HierarchicalStreamDriver driver)
			throws UnsupportedEncodingException {
		String expectedXmlData = "" + "<UTF8TestObject>\n"
				+ "  <data>\u5377</data>\n" + "</UTF8TestObject>";

		XStream xstream = new XStream(driver);
		xstream.alias("UTF8TestObject", UTF8TestObject.class);
		UTF8TestObject obj = new UTF8TestObject();
		obj.data = "\u5377";

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		xstream.toXML(obj, bos);

		assertByteArrayEquals(expectedXmlData.getBytes("UTF-8"), bos
				.toByteArray());

		Object restored = xstream.fromXML(new ByteArrayInputStream(bos
				.toByteArray()));
		Assert.assertEquals(obj, restored);

	}

	private void assertByteArrayEquals(byte expected[], byte actual[]) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
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

	public static Test suite() {
		return new UTF8EncodingTestSuite();
	}

}