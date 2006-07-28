package com.thoughtworks.xstream.converters;

public interface MarshallingContext extends DataHolder {

	/**
	 * Converts another object searching for the default converter
	 * @param nextItem	the next item to convert
	 */
    void convertAnother(Object nextItem);
    
    /**
     * Converts another object using the specified converter
     * @param nextItem	the next item to convert
     * @param converter	the Converter to use
     * @since 1.2
     */
    void convertAnother(Object nextItem, Converter converter);

}
