package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper that specifies which types are basic immutable types. Types that are marked as immutable will be written
 * multiple times in the serialization stream without using references.
 *
 * @author Joe Walnes
 */
public class ImmutableTypesMapper extends MapperWrapper {

    private final Set immutableTypes = new HashSet();

    public ImmutableTypesMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #ImmutableTypesMapper(Mapper)}
     */
    public ImmutableTypesMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
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
