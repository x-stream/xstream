package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

import java.io.File;

/**
 * This converter will take care of storing and retrieving File with either
 * an absolute path OR a relative path depending on how they were created.
 *
 * @author Joe Walnes
 */
public class FileConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(File.class);
    }

    protected Object fromString(String str) {
        return new File(str);
    }

    protected String toString(Object obj) {
        return ((File) obj).getPath();
    }

}