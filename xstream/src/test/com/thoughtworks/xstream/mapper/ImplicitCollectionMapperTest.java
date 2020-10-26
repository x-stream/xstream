/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.mapper;

import java.util.Map;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.SampleMaps;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;

import junit.framework.TestCase;

public class ImplicitCollectionMapperTest extends TestCase {

    private ImplicitCollectionMapper implicitCollections = new ImplicitCollectionMapper(
        new DefaultMapper(new ClassLoaderReference(null)), JVM.newReflectionProvider());

    public void testAllowsFieldsToBeMarkedAsImplicitCollectionsToBeAdded() {
        implicitCollections.add(SampleLists.class, "good", null);
        assertNotNull(implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good"));
        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, null));
    }

    public void testDoesNotMarkFieldsAsImplicitCollectionByDefault() {
        assertNull(implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good"));
        assertEquals(null, implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, null));
    }

    public void testAllowsFieldsToBeMarkedBasedOnItemType() {
        implicitCollections.add(SampleLists.class, "good", Software.class);
        implicitCollections.add(SampleLists.class, "bad", Hardware.class);
        assertNotNull(implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "bad"));
        assertNotNull(implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good"));
        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Software.class, null));
        assertEquals("bad", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Hardware.class, null));
    }

    public void testIncludesSubClassesWhenCheckingItemType() {
        implicitCollections.add(SampleLists.class, "good", Software.class);
        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, OpenSourceSoftware.class, null));
        assertEquals(null, implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Hardware.class, null));
    }

    public void testAllowsFieldsToBeMarkedAsNamedImplicitCollectionsToBeAdded() {
        implicitCollections.add(SampleLists.class, "good", "good-item", Object.class);
        implicitCollections.add(SampleLists.class, "bad", null);
        Mapper.ImplicitCollectionMapping mappingGood = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good");
        assertNotNull(mappingGood);
        assertEquals("good-item", mappingGood.getItemFieldName());
        assertEquals(Object.class, mappingGood.getItemType());
        assertEquals("good", mappingGood.getFieldName());

        Mapper.ImplicitCollectionMapping mappingBad = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "bad");
        assertNotNull(mappingBad);
        assertNull(mappingBad.getItemFieldName());
        assertNull(mappingBad.getItemType());

        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, "good-item"));
        assertEquals("bad", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, null));
    }

    public void testAllowsFieldsToBeMarkedBasedOnItemFieldName() {
        implicitCollections.add(SampleLists.class, "good", "good-item", Object.class);
        implicitCollections.add(SampleLists.class, "bad", "bad-item", Object.class);
        Mapper.ImplicitCollectionMapping mappingGood = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good");
        assertNotNull(mappingGood);
        assertEquals("good-item", mappingGood.getItemFieldName());
        assertEquals(Object.class, mappingGood.getItemType());
        assertEquals("good", mappingGood.getFieldName());

        Mapper.ImplicitCollectionMapping mappingBad = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "bad");
        assertNotNull(mappingBad);
        assertEquals("bad-item", mappingBad.getItemFieldName());
        assertEquals(Object.class, mappingBad.getItemType());
        assertEquals("bad", mappingBad.getFieldName());

        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, "good-item"));
        assertEquals("bad", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, "bad-item"));
    }

    public void testIncludesSubClassesWhenCheckingItemTypeForNamedImplicitCollections() {
        implicitCollections.add(SampleLists.class, "good", "good-item", Software.class);
        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, OpenSourceSoftware.class, "good-item"));
        assertEquals(null, implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Hardware.class, null));
    }

    public void testGetItemTypeForItemFieldName() {
        implicitCollections.add(SampleLists.class, "good", "good-item", Software.class);
        implicitCollections.add(SampleLists.class, "bad", "bad-item", Product.class);

        assertEquals(Software.class, implicitCollections.getItemTypeForItemFieldName(SampleLists.class, "good-item"));
        assertEquals(Product.class, implicitCollections.getItemTypeForItemFieldName(SampleLists.class, "bad-item"));
    }

    public void testAllowsFieldsToBeMarkedAsImplicitMapsToBeAdded() {
        implicitCollections.add(SampleMaps.class, "good", null);
        assertNotNull(implicitCollections.getImplicitCollectionDefForFieldName(SampleMaps.class, "good"));
        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleMaps.class, Map.Entry.class, null));
    }

    public void testGetKeyFieldNameForItemFieldName() {
        implicitCollections.add(SampleMaps.class, "good", "good-item", Software.class, "name");
        implicitCollections.add(SampleMaps.class, "bad", "bad-item", Software.class, "vendor");

        assertEquals("name", implicitCollections.getImplicitCollectionDefForFieldName(SampleMaps.class, "good").getKeyFieldName());
        assertEquals("vendor", implicitCollections.getImplicitCollectionDefForFieldName(SampleMaps.class, "bad").getKeyFieldName());
    }
}
