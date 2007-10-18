package com.thoughtworks.xstream.io.json;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;


/**
 * Testing serialization to and from JSON with Jettison driver.
 * 
 * @author Dejan Bosanac
 */
public class JettisonMappedXmlDriverTest extends TestCase {

    private String simpleJson = "{\"product\":{\"name\":\"Banana\",\"id\":\"123\",\"price\":\"23.0\"}}";
    private String hiearchyJson = "{\"category\":{\"name\":\"fruit\",\"id\":\"111\",\"products\":{\"product\":[{\"name\":\"Banana\",\"id\":\"123\",\"price\":\"23.0\",\"tags\":{\"string\":[\"yellow\",\"fresh\",\"tasty\"]}},{\"name\":\"Mango\",\"id\":\"124\",\"price\":\"34.0\"}]}}}";
    private XStream xstream;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.alias("category", Category.class);
        xstream.alias("product", Product.class);
    }

    public void testReadSimple() {
        Product product = (Product)xstream.fromXML(simpleJson);
        assertEquals(product.getName(), "Banana");
        assertEquals(product.getId(), "123");
        assertEquals("" + product.getPrice(), "" + 23.0);
    }

    public void testWriteSimple() {
        Product product = new Product("Banana", "123", 23.00);
        String result = xstream.toXML(product);
        assertEquals(result, simpleJson);
    }

    public void testWriteHierarchy() {
        Category category = new Category("fruit", "111");
        ArrayList products = new ArrayList();
        Product banana = new Product("Banana", "123", 23.00);
        ArrayList bananaTags = new ArrayList();
        bananaTags.add("yellow");
        bananaTags.add("fresh");
        bananaTags.add("tasty");
        banana.setTags(bananaTags);
        products.add(banana);
        Product mango = new Product("Mango", "124", 34.00);
        products.add(mango);
        category.setProducts(products);
        String result = xstream.toXML(category);
        assertEquals(result, hiearchyJson);
    }

    public void testHierarchyRead() {
        Category parsedCategory = (Category)xstream.fromXML(hiearchyJson);
        Product parsedBanana = (Product)parsedCategory.getProducts().get(0);
        assertEquals(parsedBanana.getName(), "Banana");
        assertEquals(parsedBanana.getTags().size(), 3);
        assertEquals(parsedBanana.getTags().get(0), "yellow");
        assertEquals(parsedBanana.getTags().get(2), "tasty");
    }

}
