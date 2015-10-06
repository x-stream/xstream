/*
 * Copyright (C) 2013, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. April 2013 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.core.util.OrderRetainingMap;

public class ImplicitTest extends AbstractAcceptanceTest {
   
    public static class AllImplicitTypes {
        
        public static class A {
            public int val;
        }
        
        public static class B {
            public int val;
        }
        
        public static class C {
            public Integer val;
        }
        
        public A[] aArray = new A[2];
        public String separator1 = "--1--";
        public List bList = new ArrayList();
        public String separator2 = "--2--";
        public Map cMap = new OrderRetainingMap();
    }
    
    public void testAllImplicitTypesAtOnceWithImplicitElementTypes()
    {
        xstream.alias("implicits", AllImplicitTypes.class);
        xstream.alias("a", AllImplicitTypes.A.class);
        xstream.alias("b", AllImplicitTypes.B.class);
        xstream.alias("c", AllImplicitTypes.C.class);
        xstream.addDefaultImplementation(OrderRetainingMap.class, Map.class);
        xstream.addImplicitArray(AllImplicitTypes.class, "aArray");
        xstream.addImplicitCollection(AllImplicitTypes.class, "bList");
        xstream.addImplicitMap(AllImplicitTypes.class, "cMap", AllImplicitTypes.C.class, "val");
        String expected = ""
            + "<implicits>\n"
            + "  <a>\n"
            + "    <val>1</val>\n"
            + "  </a>\n"
            + "  <a>\n"
            + "    <val>2</val>\n"
            + "  </a>\n"
            + "  <separator1>--1--</separator1>\n"
            + "  <b>\n"
            + "    <val>3</val>\n"
            + "  </b>\n"
            + "  <b>\n"
            + "    <val>4</val>\n"
            + "  </b>\n"
            + "  <separator2>--2--</separator2>\n"
            + "  <c>\n"
            + "    <val>5</val>\n"
            + "  </c>\n"
            + "  <c>\n"
            + "    <val>6</val>\n"
            + "  </c>\n"
            + "</implicits>";

        AllImplicitTypes implicits = new AllImplicitTypes();
        implicits.aArray[0] = new AllImplicitTypes.A();
        implicits.aArray[0].val = 1;
        implicits.aArray[1] = new AllImplicitTypes.A();
        implicits.aArray[1].val = 2;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(0)).val = 3;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(1)).val = 4;
        AllImplicitTypes.C c = new AllImplicitTypes.C();
        c.val = new Integer(5);
        implicits.cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(6);
        implicits.cMap.put(c.val, c);
        assertBothWays(implicits, expected);
    }
    
    public void testAllImplicitTypesAtOnceWithExplicitElementTypes()
    {
        xstream.alias("implicits", AllImplicitTypes.class);
        xstream.alias("a", AllImplicitTypes.A.class);
        xstream.alias("b", AllImplicitTypes.B.class);
        xstream.alias("c", AllImplicitTypes.C.class);
        xstream.addDefaultImplementation(OrderRetainingMap.class, Map.class);
        xstream.addImplicitArray(AllImplicitTypes.class, "aArray");
        xstream.addImplicitCollection(AllImplicitTypes.class, "bList", AllImplicitTypes.B.class);
        xstream.addImplicitMap(AllImplicitTypes.class, "cMap", AllImplicitTypes.C.class, "val");
        String expected = ""
            + "<implicits>\n"
            + "  <a>\n"
            + "    <val>1</val>\n"
            + "  </a>\n"
            + "  <a>\n"
            + "    <val>2</val>\n"
            + "  </a>\n"
            + "  <separator1>--1--</separator1>\n"
            + "  <b>\n"
            + "    <val>3</val>\n"
            + "  </b>\n"
            + "  <b>\n"
            + "    <val>4</val>\n"
            + "  </b>\n"
            + "  <separator2>--2--</separator2>\n"
            + "  <c>\n"
            + "    <val>5</val>\n"
            + "  </c>\n"
            + "  <c>\n"
            + "    <val>6</val>\n"
            + "  </c>\n"
            + "</implicits>";

        AllImplicitTypes implicits = new AllImplicitTypes();
        implicits.aArray[0] = new AllImplicitTypes.A();
        implicits.aArray[0].val = 1;
        implicits.aArray[1] = new AllImplicitTypes.A();
        implicits.aArray[1].val = 2;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(0)).val = 3;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(1)).val = 4;
        AllImplicitTypes.C c = new AllImplicitTypes.C();
        c.val = new Integer(5);
        implicits.cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(6);
        implicits.cMap.put(c.val, c);
        assertBothWays(implicits, expected);
        implicits.separator1 = implicits.separator2 = null;
        assertBothWays(implicits, stripSeparator(expected));
        implicits.separator1 = implicits.separator2 = null;
        assertBothWays(implicits, stripSeparator(expected));
    }
    
    public void testAllImplicitTypesAtOnceWithExplicitElementNames()
    {
        xstream.alias("implicits", AllImplicitTypes.class);
        xstream.addDefaultImplementation(OrderRetainingMap.class, Map.class);
        xstream.addImplicitArray(AllImplicitTypes.class, "aArray", "a");
        xstream.addImplicitCollection(AllImplicitTypes.class, "bList", "b", AllImplicitTypes.B.class);
        xstream.addImplicitMap(AllImplicitTypes.class, "cMap", "c", AllImplicitTypes.C.class, "val");
        String expected = ""
            + "<implicits>\n"
            + "  <a>\n"
            + "    <val>1</val>\n"
            + "  </a>\n"
            + "  <a>\n"
            + "    <val>2</val>\n"
            + "  </a>\n"
            + "  <separator1>--1--</separator1>\n"
            + "  <b>\n"
            + "    <val>3</val>\n"
            + "  </b>\n"
            + "  <b>\n"
            + "    <val>4</val>\n"
            + "  </b>\n"
            + "  <separator2>--2--</separator2>\n"
            + "  <c>\n"
            + "    <val>5</val>\n"
            + "  </c>\n"
            + "  <c>\n"
            + "    <val>6</val>\n"
            + "  </c>\n"
            + "</implicits>";

        AllImplicitTypes implicits = new AllImplicitTypes();
        implicits.aArray[0] = new AllImplicitTypes.A();
        implicits.aArray[0].val = 1;
        implicits.aArray[1] = new AllImplicitTypes.A();
        implicits.aArray[1].val = 2;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(0)).val = 3;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(1)).val = 4;
        AllImplicitTypes.C c = new AllImplicitTypes.C();
        c.val = new Integer(5);
        implicits.cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(6);
        implicits.cMap.put(c.val, c);
        assertBothWays(implicits, expected);
        implicits.separator1 = implicits.separator2 = null;
        assertBothWays(implicits, stripSeparator(expected));
    }
    
    private String stripSeparator(String s) {
        return s.replaceAll(" *<separator.+</separator.+\n", "");
    }
    
    public static class AllHidingTypes extends AllImplicitTypes {
        public String separator = "--X--";
        public String aArray = "a";
        public String bList = "b";
        public String cMap = "c";
    }
    
    public static class AllHidingImplicitTypes extends AllHidingTypes {
        public String separator = "--H--";
        public A[] aArray = new A[2];
        public List bList = new ArrayList();
        public Map cMap = new LinkedHashMap();
    }
    
    public void testHiddenImplicitTypesAtOnceWithExplicitElementNames()
    {
        xstream.alias("implicits", AllHidingImplicitTypes.class);
        xstream.alias("hiding", AllHidingTypes.class);
        xstream.alias("hidden", AllImplicitTypes.class);
        xstream.addDefaultImplementation(LinkedHashMap.class, Map.class);
        xstream.addImplicitArray(AllImplicitTypes.class, "aArray", "aHidden");
        xstream.addImplicitArray(AllHidingImplicitTypes.class, "aArray", "a");
        xstream.addImplicitCollection(AllImplicitTypes.class, "bList", "bHidden", AllImplicitTypes.B.class);
        xstream.addImplicitCollection(AllHidingImplicitTypes.class, "bList", "b", AllImplicitTypes.B.class);
        xstream.addImplicitMap(AllImplicitTypes.class, "cMap", "cHidden", AllImplicitTypes.C.class, "val");
        xstream.addImplicitMap(AllHidingImplicitTypes.class, "cMap", "c", AllImplicitTypes.C.class, "val");
        String expected = ""
            + "<implicits>\n"
            + "  <aHidden defined-in=\"hidden\">\n"
            + "    <val>1</val>\n"
            + "  </aHidden>\n"
            + "  <aHidden defined-in=\"hidden\">\n"
            + "    <val>2</val>\n"
            + "  </aHidden>\n"
            + "  <separator1>--1--</separator1>\n"
            + "  <bHidden defined-in=\"hidden\">\n"
            + "    <val>3</val>\n"
            + "  </bHidden>\n"
            + "  <bHidden defined-in=\"hidden\">\n"
            + "    <val>4</val>\n"
            + "  </bHidden>\n"
            + "  <separator2>--2--</separator2>\n"
            + "  <cHidden defined-in=\"hidden\">\n"
            + "    <val>5</val>\n"
            + "  </cHidden>\n"
            + "  <cHidden defined-in=\"hidden\">\n"
            + "    <val>6</val>\n"
            + "  </cHidden>\n"
            + "  <separator defined-in=\"hiding\">--X--</separator>\n"
            + "  <aArray defined-in=\"hiding\">a</aArray>\n"
            + "  <bList defined-in=\"hiding\">b</bList>\n"
            + "  <cMap defined-in=\"hiding\">c</cMap>\n"
            + "  <separator>--H--</separator>\n"
            + "  <a>\n"
            + "    <val>7</val>\n"
            + "  </a>\n"
            + "  <a>\n"
            + "    <val>8</val>\n"
            + "  </a>\n"
            + "  <b>\n"
            + "    <val>9</val>\n"
            + "  </b>\n"
            + "  <b>\n"
            + "    <val>10</val>\n"
            + "  </b>\n"
            + "  <c>\n"
            + "    <val>11</val>\n"
            + "  </c>\n"
            + "  <c>\n"
            + "    <val>12</val>\n"
            + "  </c>\n"
            + "</implicits>";

        AllHidingImplicitTypes implicits = new AllHidingImplicitTypes();
        ((AllImplicitTypes)implicits).aArray[0] = new AllImplicitTypes.A();
        ((AllImplicitTypes)implicits).aArray[0].val = 1;
        ((AllImplicitTypes)implicits).aArray[1] = new AllImplicitTypes.A();
        ((AllImplicitTypes)implicits).aArray[1].val = 2;
        implicits.aArray[0] = new AllImplicitTypes.A();
        implicits.aArray[0].val = 7;
        implicits.aArray[1] = new AllImplicitTypes.A();
        implicits.aArray[1].val = 8;
        ((AllImplicitTypes)implicits).bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)((AllImplicitTypes)implicits).bList.get(0)).val = 3;
        ((AllImplicitTypes)implicits).bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)((AllImplicitTypes)implicits).bList.get(1)).val = 4;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(0)).val = 9;
        implicits.bList.add(new AllImplicitTypes.B());
        ((AllImplicitTypes.B)implicits.bList.get(1)).val = 10;
        AllImplicitTypes.C c = new AllImplicitTypes.C();
        c.val = new Integer(5);
        ((AllImplicitTypes)implicits).cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(6);
        ((AllImplicitTypes)implicits).cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(11);
        implicits.cMap.put(c.val, c);
        c = new AllImplicitTypes.C();
        c.val = new Integer(12);
        implicits.cMap.put(c.val, c);
        assertBothWays(implicits, expected);
        implicits.separator1 = implicits.separator2 = ((AllHidingTypes)implicits).separator = implicits.separator = null;
        assertBothWays(implicits, stripSeparator(expected));
    }
}
