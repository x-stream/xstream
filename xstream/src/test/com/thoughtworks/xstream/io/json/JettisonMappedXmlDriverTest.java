/*
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013, 2014, 2016, 2017, 2018 XStream Committers.
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.codehaus.jettison.mapped.Configuration;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.OwnerOfExternalizable;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.acceptance.objects.SomethingExternalizable;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;


/**
 * Testing serialization to and from JSON with Jettison driver.
 *
 * @author Dejan Bosanac
 */
public class JettisonMappedXmlDriverTest extends TestCase {

    private final static String SIMPLE = "{'product':{'name':'Banana','id':123,'price':23}}".replace('\'', '"');
    private final static String HIERARCHY =
            "{'category':{'name':'fruit','id':111,'products':[{'product':[{'name':'Banana','id':123,'price':23.01,'tags':[{'string':['yellow','fresh','tasty']}]},{'name':'Mango','id':124,'price':34.01}]}]}}"
                .replace('\'', '"');

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneChanger.change("UTC");
        xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("category", Category.class);
        xstream.alias("product", Product.class);
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testReadSimple() {
        final Product product = (Product)xstream.fromXML(SIMPLE);
        assertEquals(product.getName(), "Banana");
        assertEquals(product.getId(), "123");
        assertEquals("" + product.getPrice(), "" + 23.00);
    }

    public void testWriteSimple() {
        final Product product = new Product("Banana", "123", 23.00);
        final String result = xstream.toXML(product);
        assertEquals(SIMPLE, result);
    }

    public void testJettisonConfigured()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        final Object typeConverter = Class.forName("org.codehaus.jettison.mapped.SimpleConverter").newInstance();
        final Method setTypeConverter = Configuration.class.getMethod("setTypeConverter", new Class[]{
            typeConverter.getClass().getInterfaces()[0]});
        final Configuration config = new Configuration();
        setTypeConverter.invoke(config, new Object[]{typeConverter});
        xstream = new XStream(new JettisonMappedXmlDriver(config));
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("product", Product.class);
        final Product product = new Product("Banana", "123", 23.00);
        final String result = xstream.toXML(product);
        assertEquals("{'product':{'name':'Banana','id':'123','price':'23.0'}}".replace('\'', '"'), result);
        assertEquals(product, xstream.fromXML(result));
    }

    public void testWriteHierarchy() {
        final Category<Product> category = new Category<>("fruit", "111");
        final ArrayList<Product> products = new ArrayList<>();
        final Product banana = new Product("Banana", "123", 23.01);
        final ArrayList<String> bananaTags = new ArrayList<>();
        bananaTags.add("yellow");
        bananaTags.add("fresh");
        bananaTags.add("tasty");
        banana.setTags(bananaTags);
        products.add(banana);
        final Product mango = new Product("Mango", "124", 34.01);
        products.add(mango);
        category.setProducts(products);
        final String result = xstream.toXML(category);
        assertEquals(HIERARCHY, result);
    }

    public void testHierarchyRead() {
        final Category<Product> parsedCategory = xstream.<Category<Product>>fromXML(HIERARCHY);
        final Product parsedBanana = parsedCategory.getProducts().get(0);
        assertEquals("Banana", parsedBanana.getName());
        assertEquals(3, parsedBanana.getTags().size());
        assertEquals("yellow", parsedBanana.getTags().get(0));
        assertEquals("tasty", parsedBanana.getTags().get(2));
    }

    public void testObjectStream() throws IOException, ClassNotFoundException {
        final Product product = new Product("Banana", "123", 23.00);
        final StringWriter writer = new StringWriter();
	try (ObjectOutputStream oos = xstream.createObjectOutputStream(writer, "oos")) {
	    oos.writeObject(product);
	}
        final String json = writer.toString();
        assertEquals("{\"oos\":" + SIMPLE + "}", json);
        try (final ObjectInputStream ois = xstream.createObjectInputStream(new StringReader(json))) {
            final Product parsedProduct = (Product)ois.readObject();
            assertEquals(product.toString(), parsedProduct.toString());
        }
    }

    public void testDoesHandleQuotesAndEscapes() {
        final String[] strings = new String[]{
            "last\"", "\"first", "\"between\"", "around \"\" it", "back\\slash", "forward/slash"};
        final String expected = (""
            + "{#string-array#:[{#string#:["
            + "#last\\\"#,"
            + "#\\\"first#,"
            + "#\\\"between\\\"#,"
            + "#around \\\"\\\" it#,"
            + "#back\\\\slash#,"
            + "#forward\\/slash#]}]}").replace('#', '"');
        assertEquals(expected, xstream.toXML(strings));
    }

    public void testDoesEscapeValuesAccordingRfc4627() {
        final String expected = "{'string':'\\u0000\\u0001\\u001f \uffee'}".replace('\'', '"');
        assertEquals(expected, xstream.toXML("\u0000\u0001\u001f\u0020\uffee"));
    }

    public void testListWithOneSimpleObject() {
        final ArrayList<String> list1 = new ArrayList<>();
        list1.add("one");
        final String json = xstream.toXML(list1);
        assertEquals("{'list':[{'string':'one'}]}".replace('\'', '"'), json);
        final ArrayList<String> list2 = xstream.<ArrayList<String>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void testListWithSimpleObjects() {
        final ArrayList<String> list1 = new ArrayList<>();
        list1.add("one");
        list1.add("two");
        list1.add("three");
        final String json = xstream.toXML(list1);
        assertEquals("{'list':[{'string':['one','two','three']}]}".replace('\'', '"'), json);
        final ArrayList<String> list2 = xstream.<ArrayList<String>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void testListWithDifferentSimpleObjects() {
        final ArrayList<Object> list1 = new ArrayList<>();
        list1.add("one");
        list1.add(2);
        list1.add(3.3f);
        final String json = xstream.toXML(list1);
        assertEquals("{'list':[{'string':'one','int':2,'float':3.3}]}".replace('\'', '"'), json);
        final ArrayList<Object> list2 = xstream.<ArrayList<Object>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void testSingletonListWithComplexObject() {
        final Product product = new Product("Banana", "123", 23.00);
        final ArrayList<Product> list1 = new ArrayList<>();
        list1.add(product);
        final String json = xstream.toXML(list1);
        assertEquals("{'list':[{'product':{'name':'Banana','id':123,'price':23}}]}".replace('\'', '"'), json);
        final ArrayList<Product> list2 = xstream.<ArrayList<Product>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void testListWithComplexNestedObjects() {
        final ArrayList<Product> list1 = new ArrayList<>();
        list1.add(new Product("Banana", "123", 23.00));
        list1.add(new Product("Apple", "47", 11.00));
        list1.add(new Product("Orange", "100", 42.00));
        final ArrayList<Product> tags = new ArrayList<>();
        list1.get(1).setTags(tags);
        tags.add(new Product("Braeburn", "47.1", 10.00));
        final String json = xstream.toXML(list1);
        assertEquals(
            "{'list':[{'product':[{'name':'Banana','id':123,'price':23},{'name':'Apple','id':47,'price':11,'tags':[{'product':{'name':'Braeburn','id':47.1,'price':10}}]},{'name':'Orange','id':100,'price':42}]}]}"
                .replace('\'', '"'), json);
        final ArrayList<Product> list2 = xstream.<ArrayList<Product>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void todoTestEmptyList() {
        final ArrayList<?> list1 = new ArrayList<>();
        final String json = xstream.toXML(list1);
        assertEquals("{'list':[]}".replace('\'', '"'), json);
        final ArrayList<?> list2 = xstream.<ArrayList<?>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public static class Topic extends StandardObject {
        private static final long serialVersionUID = 200811L;
        long id;
        String description;
        Date createdOn;
    }

    public void testDefaultValue() {
        final Topic topic1 = new Topic();
        topic1.id = 4711;
        topic1.description = "JSON";
        topic1.createdOn = new Timestamp(1000);
        xstream.alias("topic", Topic.class);
        final String json = xstream.toXML(topic1);
        assertEquals(
            "{'topic':{'id':4711,'description':'JSON','createdOn':{'@class':'sql-timestamp','$':'1970-01-01 00:00:01'}}}"
                .replace('\'', '"'), json);
        final Topic topic2 = (Topic)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(topic2));
    }

    public void testLongValueWithHighPrecision() {
        final Topic topic1 = new Topic();
        topic1.id = Long.MAX_VALUE;
        topic1.description = "JSON";
        xstream.alias("topic", Topic.class);
        final String json = xstream.toXML(topic1);
        assertEquals("{'topic':{'id':9223372036854775807,'description':'JSON'}}".replace('\'', '"'), json);
        final Topic topic2 = (Topic)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(topic2));
    }

    public void testEmbeddedXml() {
        final ArrayList<String> list1 = new ArrayList<>();
        list1.add("<xml attribute=\"foo\"><![CDATA[&quot;\"\'<>]]></xml>");
        final String json = xstream.toXML(list1);
        assertEquals("{\"list\":[{\"string\":\"<xml attribute=\\\"foo\\\"><![CDATA[&quot;\\\"'<>]]><\\/xml>\"}]}",
            json);
        final ArrayList<String> list2 = xstream.<ArrayList<String>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    public void testArrayList() {
        final ArrayList<Object> list1 = new ArrayList<>();
        list1.add(12);
        list1.add("string");
        list1.add(13);
        final String json = xstream.toXML(list1);
        final ArrayList<?> list2 = xstream.<ArrayList<?>>fromXML(json);
        assertEquals(json, xstream.toXML(list2));
    }

    private static class SpecialCharacters extends StandardObject {
        private static final long serialVersionUID = 201010L;
        @SuppressWarnings("unused")
        String _foo__$_;
    }

    public void testSpecialNames() {
        final SpecialCharacters sc = new SpecialCharacters();
        sc._foo__$_ = "bar";
        final String json = xstream.toXML(sc);
        assertEquals(
            "{'com.thoughtworks.xstream.io.json.JettisonMappedXmlDriverTest$SpecialCharacters':{'_foo__$_':'bar'}}"
                .replace('\'', '"'), json);
        final SpecialCharacters sc2 = (SpecialCharacters)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(sc2));
    }

    public void testProperties() {
        final Properties properties = new Properties();
        properties.setProperty("key.1", "Value 1");
        String json = xstream.toXML(properties);
        assertEquals("{'properties':[{'property':{'@name':'key.1','@value':'Value 1'}}]}".replace('\'', '"'), json);
        Properties properties2 = (Properties)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(properties2));

        properties.setProperty("key.2", "Value 2");
        json = xstream.toXML(properties);
        assertEquals(
            "{'properties':[{'property':[{'@name':'key.2','@value':'Value 2'},{'@name':'key.1','@value':'Value 1'}]}]}"
                .replace('\'', '"'), json);
        properties2 = (Properties)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(properties2));
    }

    public void testEmptyArray() {
        xstream.alias("exception", Exception.class);
        final Exception[] exceptions = new Exception[3];
        final String json = xstream.toXML(exceptions);
        assertEquals("{'exception-array':[{'null':['','','']}]}".replace('\'', '"'), json);
        final Exception[] exceptions2 = (Exception[])xstream.fromXML(json);
        assertEquals(json, xstream.toXML(exceptions2));
    }

    public void todoTestCanMarshalEmbeddedExternalizable() {
        xstream.alias("owner", OwnerOfExternalizable.class);

        final OwnerOfExternalizable in = new OwnerOfExternalizable();
        in.target = new SomethingExternalizable("Joe", "Walnes");
        final String json = xstream.toXML(in);
        // already wrong, Jettison reorders elements ...
        assertEquals("{'owner':{'target':{'int':3,'string':['JoeWalnes','XStream'],'null':''}}}".replace('\'', '"'),
            json);
        final OwnerOfExternalizable owner = (OwnerOfExternalizable)xstream.fromXML(json);
        assertEquals(json, xstream.toXML(owner));
        assertEquals(in.target, owner.target);
    }
}
