package com.thoughtworks.xstream.core;

import junit.framework.TestCase;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.xstream.alias.ImplicitCollectionDef;

public class AddableImplicitCollectionMapperTest extends TestCase {

    private AddableImplicitCollectionMapper implicitCollections = new AddableImplicitCollectionMapper();

    public void testAllowsFieldsToBeMarkedAsImplicitCollectionsToBeAdded() {
        implicitCollections.add(SampleLists.class, "good", Object.class);
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
        implicitCollections.add(SampleLists.class, "bad", Object.class);
        ImplicitCollectionDef defGood = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good");
        assertNotNull(defGood);
        assertEquals("good-item", defGood.getItemFieldName());
        assertEquals(Object.class, defGood.getItemType());
        assertEquals("good", defGood.getFieldName());

        ImplicitCollectionDef defBad = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "bad");
        assertNotNull(defBad);
        assertNull(defBad.getItemFieldName());

        assertEquals("good", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, "good-item"));
        assertEquals("bad", implicitCollections.getFieldNameForItemTypeAndName(SampleLists.class, Object.class, null));
    }

    public void testAllowsFieldsToBeMarkedBasedOnItemFieldName() {
        implicitCollections.add(SampleLists.class, "good", "good-item", Object.class);
        implicitCollections.add(SampleLists.class, "bad", "bad-item", Object.class);
        ImplicitCollectionDef defGood = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "good");
        assertNotNull(defGood);
        assertEquals("good-item", defGood.getItemFieldName());
        assertEquals(Object.class, defGood.getItemType());
        assertEquals("good", defGood.getFieldName());

        ImplicitCollectionDef defBad = implicitCollections.getImplicitCollectionDefForFieldName(SampleLists.class, "bad");
        assertNotNull(defBad);
        assertEquals("bad-item", defBad.getItemFieldName());
        assertEquals(Object.class, defBad.getItemType());
        assertEquals("bad", defBad.getFieldName());

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
        implicitCollections.add(SampleLists.class, "bad", "bad-item", Software.class);

        assertEquals(Software.class, implicitCollections.getItemTypeForItemFieldName(SampleLists.class, "good-item"));
    }
}
