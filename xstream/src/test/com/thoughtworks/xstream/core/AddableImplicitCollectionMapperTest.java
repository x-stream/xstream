package com.thoughtworks.xstream.core;

import junit.framework.TestCase;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.OpenSourceSoftware;

public class AddableImplicitCollectionMapperTest extends TestCase {

    private AddableImplicitCollectionMapper implicitCollections = new AddableImplicitCollectionMapper();

    public void testAllowsFieldsToBeMarkedAsImplicitCollectionsToBeAdded() {
        implicitCollections.add(SampleLists.class, "good", Object.class);
        assertEquals(true, implicitCollections.isImplicitCollectionField(SampleLists.class, "good"));
        assertEquals("good", implicitCollections.implicitCollectionFieldForType(SampleLists.class, Object.class));
    }

    public void testDoesNotMarkFieldsAsImplicitCollectionByDefault() {
        assertEquals(false, implicitCollections.isImplicitCollectionField(SampleLists.class, "good"));
        assertEquals(null, implicitCollections.implicitCollectionFieldForType(SampleLists.class, Object.class));
    }

    public void testAllowsFieldsToBeMarkedBasedOnItemType() {
        implicitCollections.add(SampleLists.class, "good", Software.class);
        implicitCollections.add(SampleLists.class, "bad", Hardware.class);
        assertEquals(true, implicitCollections.isImplicitCollectionField(SampleLists.class, "bad"));
        assertEquals(true, implicitCollections.isImplicitCollectionField(SampleLists.class, "good"));
        assertEquals("good", implicitCollections.implicitCollectionFieldForType(SampleLists.class, Software.class));
        assertEquals("bad", implicitCollections.implicitCollectionFieldForType(SampleLists.class, Hardware.class));
    }

    public void testIncludesSubClassesWhenCheckingItemType() {
        implicitCollections.add(SampleLists.class, "good", Software.class);
        assertEquals("good", implicitCollections.implicitCollectionFieldForType(SampleLists.class, OpenSourceSoftware.class));
        assertEquals(null, implicitCollections.implicitCollectionFieldForType(SampleLists.class, Hardware.class));
    }

}
