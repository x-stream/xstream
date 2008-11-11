/*
 * Copyright (C) 2007, 2008 XStream Committers.
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;


/**
 * Testing serialization to and from JSON with Jettison driver.
 * 
 * @author Dejan Bosanac
 */
public class JettisonMappedXmlDriverTest extends TestCase {

    private final static String SIMPLE = "{'product':{'name':'Banana','id':123,'price':23}}"
        .replace('\'', '"');
    private final static String HIERARCHY = "{'category':{'name':'fruit','id':111,'products':{'product':[{'name':'Banana','id':123,'price':23.01,'tags':{'string':['yellow','fresh','tasty']}},{'name':'Mango','id':124,'price':34.01}]}}}"
        .replace('\'', '"');

    private XStream xstream;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneChanger.change("UTC");
        xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.alias("category", Category.class);
        xstream.alias("product", Product.class);
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testReadSimple() {
        Product product = (Product)xstream.fromXML(SIMPLE);
        assertEquals(product.getName(), "Banana");
        assertEquals(product.getId(), "123");
        assertEquals("" + product.getPrice(), "" + 23.00);
    }

    public void testWriteSimple() {
        Product product = new Product("Banana", "123", 23.00);
        String result = xstream.toXML(product);
        assertEquals(SIMPLE, result);
    }

    public void testWriteHierarchy() {
        Category category = new Category("fruit", "111");
        ArrayList products = new ArrayList();
        Product banana = new Product("Banana", "123", 23.01);
        ArrayList bananaTags = new ArrayList();
        bananaTags.add("yellow");
        bananaTags.add("fresh");
        bananaTags.add("tasty");
        banana.setTags(bananaTags);
        products.add(banana);
        Product mango = new Product("Mango", "124", 34.01);
        products.add(mango);
        category.setProducts(products);
        String result = xstream.toXML(category);
        assertEquals(HIERARCHY, result);
    }

    public void testHierarchyRead() {
        Category parsedCategory = (Category)xstream.fromXML(HIERARCHY);
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
        assertEquals("{\"oos\":" + SIMPLE + "}", json);
        ObjectInputStream ois = xstream.createObjectInputStream(new StringReader(json));
        Product parsedProduct = (Product)ois.readObject();
        assertEquals(product.toString(), parsedProduct.toString());
    }

    public void testDoesHandleQuotesAndEscapes() {
        String[] strings = new String[]{"last\"", "\"first", "\"between\"", "around \"\" it", "back\\slash",};
        String expected = (""
                + "{#string-array#:{#string#:["
                + "#last\\\"#,"
                + "#\\\"first#,"
                + "#\\\"between\\\"#,"
                + "#around \\\"\\\" it#,"
                + "#back\\\\slash#"
                + "]}}").replace('#', '"');
        assertEquals(expected, xstream.toXML(strings));
    }
    
    public void testDoesEscapeValuesAccordingRfc4627() {
        String expected = "{'string':'\\u0000\\u0001\\u001f \uffee'}".replace('\'', '"');
        assertEquals(expected, xstream.toXML("\u0000\u0001\u001f\u0020\uffee"));
    }

    public void testOneElementList() {
        ArrayList list1 = new ArrayList();
        list1.add("one");
        String json = xstream.toXML(list1);
        assertEquals("{\"list\":{\"string\":[\"one\"]}}", json);
        ArrayList list2 = (ArrayList)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void todoTestEmptyList() {
        ArrayList list1 = new ArrayList();
        String json = xstream.toXML(list1);
        assertEquals("{\"list\":[]}", json);
        ArrayList list2 = (ArrayList)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }
    
    public static class Topic extends StandardObject {
        long id;
        String description;
        Date createdOn;
    }
    
    public void testDefaultValue() {
        Topic topic1 = new Topic();
        topic1.id = 4711;
        topic1.description = "JSON";
        topic1.createdOn = new Timestamp(1000);
        xstream.alias("topic", Topic.class);
        String json = xstream.toXML(topic1);
        assertEquals(
            "{\"topic\":{\"id\":4711,\"description\":\"JSON\",\"createdOn\":{\"@class\":\"sql-timestamp\",\"$\":\"1970-01-01 00:00:01.0\"}}}",
            json);
        Topic topic2 = (Topic)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(topic2));
    }

    // TODO: See XSTR-460
    public void todoTestArrayList() throws IOException {
        ArrayList list1 = new ArrayList();
        list1.clear();
        list1.add(new Integer(12));

        list1.add("string");
        list1.add(new Integer(13));
        // StringWriter writer = new StringWriter();
        // xstream.marshal(list1, new JsonHierarchicalStreamWriter(writer));
        // writer.close();
        // String json = writer.toString();
        String json = xstream.toXML(list1);

        ArrayList list2 = (ArrayList)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }
}
