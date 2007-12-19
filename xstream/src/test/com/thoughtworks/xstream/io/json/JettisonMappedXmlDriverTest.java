/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. April 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
        assertEquals(simpleJson, result);
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
        assertEquals(hiearchyJson, result);
    }

    public void testHierarchyRead() {
        Category parsedCategory = (Category)xstream.fromXML(hiearchyJson);
        Product parsedBanana = (Product)parsedCategory.getProducts().get(0);
        assertEquals("Banana", parsedBanana.getName());
        assertEquals(3, parsedBanana.getTags().size());
        assertEquals("yellow", parsedBanana.getTags().get(0));
        assertEquals("tasty", parsedBanana.getTags().get(2));
    }

    public void testObjectStream() throws IOException, ClassNotFoundException {
        Product product = new Product("Banana", "123", 23.00);
        StringWriter writer = new StringWriter();
        ObjectOutputStream oos = xstream.createObjectOutputStream(writer, "oos");
        oos.writeObject(product);
        oos.close();
        String json = writer.toString();
        assertEquals("{\"oos\":" +simpleJson + "}", json);
        ObjectInputStream ois = xstream.createObjectInputStream(new StringReader(json));
        Product parsedProduct = (Product)ois.readObject();
        assertEquals(product.toString(), parsedProduct.toString());
    }
    
    // TODO: See XSTR-460
    public void todoTestArrayList() throws IOException {
        ArrayList list1 = new ArrayList();
        list1.clear();
        list1.add(12);
       
        list1.add("string");
        list1.add(13);
//        StringWriter writer = new StringWriter();
//        xstream.marshal(list1, new JsonHierarchicalStreamWriter(writer));
//        writer.close();
//        String json = writer.toString();
        String json = xstream.toXML(list1);
       
        ArrayList list2 = (ArrayList)xstream.fromXML(json);
        assertEquals(json,xstream.toXML(list2));
       }
}
