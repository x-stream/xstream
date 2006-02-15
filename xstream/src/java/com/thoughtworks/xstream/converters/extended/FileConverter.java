package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.io.File;

/**
 * This converter will take care of storing and retrieving File with either
 * an absolute path OR a relative path depending on how they were created.
 *
 * @author Joe Walnes
 */
public class FileConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(File.class);
    }

    public Object fromString(String str) {
        return new File(str);
    }

    public String toString(Object obj) {
        return ((File) obj).getPath();
    }

}