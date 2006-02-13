package com.thoughtworks.xstream.alias;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @deprecated As of 1.2, use {@link Mapper}
 */
public interface ClassMapper extends Mapper {

    /**
     * Place holder type used for null values.
     * @deprecated As of 1.2, use {@link Mapper.Null}
     */
    class Null extends Mapper.Null {}
}
