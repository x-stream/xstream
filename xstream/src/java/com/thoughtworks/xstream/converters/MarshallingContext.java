package com.thoughtworks.xstream.converters;


public interface MarshallingContext extends DataHolder {

    void convertAnother(Object nextItem);

}
