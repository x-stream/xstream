package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

import java.util.Collection;
import java.util.Iterator;

public class CollectionConverter extends AbstractCollectionConverter {

    public CollectionConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        super(classMapper,classAttributeIdentifier);
    }

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public void toXML(MarshallingContext context) {
        Collection collection = (Collection) context.currentObject();
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            writeItem(item, context);
        }
    }

    public Object fromXML(UnmarshallingContext context) {
        Collection collection = (Collection) createCollection(context.getRequiredType());
        while (context.xmlNextChild()) {
            Object item = readItem(context);
            collection.add(item);
            context.xmlPop();
        }
        return collection;
    }

}
