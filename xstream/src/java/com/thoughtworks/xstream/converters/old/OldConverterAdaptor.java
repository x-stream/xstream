package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class OldConverterAdaptor implements Converter {

    private OldConverter old;

    public OldConverterAdaptor(OldConverter old) {
        this.old = old;
    }

    public boolean canConvert(Class type) {
        return old.canConvert(type);
    }

    public void toXML(MarshallingContext context) {
        old.toXML(
                context.getObjectTree(),
                context.getXMLWriter(),
                context.getConverterLookup()
        );
    }

    public Object fromXML(UnmarshallingContext context) {
       old.fromXML(
               context.getObjectTree(),
               context.getXMLReader(),
               context.getConverterLookup(),
               context.getRequiredType()
       );
       return context.getObjectTree().get();
    }
}
