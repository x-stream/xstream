/*
 * Copyright (C) 2013 XStream Committers.
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
    }
}
