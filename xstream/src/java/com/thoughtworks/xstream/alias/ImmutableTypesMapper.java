package com.thoughtworks.xstream.alias;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.io.File;

public class ImmutableTypesMapper extends ClassMapperWrapper {

    private final Set immutableTypes = Collections.synchronizedSet(new HashSet());

    public ImmutableTypesMapper(ClassMapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    protected void addDefaults() {
        // register immutable primitives
        addImmutableType(boolean.class);
        addImmutableType(Boolean.class);
        addImmutableType(byte.class);
        addImmutableType(Byte.class);
        addImmutableType(char.class);
        addImmutableType(Character.class);
        addImmutableType(double.class);
        addImmutableType(Double.class);
        addImmutableType(float.class);
        addImmutableType(Float.class);
        addImmutableType(int.class);
        addImmutableType(Integer.class);
        addImmutableType(long.class);
        addImmutableType(Long.class);
        addImmutableType(short.class);
        addImmutableType(Short.class);

        // register other immutable types
        addImmutableType(Null.class);
        addImmutableType(BigDecimal.class);
        addImmutableType(BigInteger.class);
        addImmutableType(String.class);
        addImmutableType(URL.class);
        addImmutableType(File.class);
        addImmutableType(Class.class);
    }

    public void addImmutableType(Class type) {
        immutableTypes.add(type);
    }

    public boolean isImmutableValueType(Class type) {
        if (immutableTypes.contains(type)) {
            return true;
        } else {
            return super.isImmutableValueType(type);
        }
    }

}
